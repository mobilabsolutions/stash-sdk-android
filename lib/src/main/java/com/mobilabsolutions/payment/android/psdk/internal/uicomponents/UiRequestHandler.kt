package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mobilabsolutions.payment.android.R
import com.mobilabsolutions.payment.android.psdk.PaymentMethodAlias
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Singleton
class UiRequestHandler @Inject constructor() {

    sealed class DataEntryResult {
        class Success : DataEntryResult()
        class Processing : DataEntryResult()
        class Failure(val throwable: Throwable) : DataEntryResult()
    }

    class EntryCancelled : RuntimeException()
    class UserCancelled : RuntimeException("User cancelled")

    @Inject
    lateinit var integrations: Map<@JvmSuppressWildcards Integration, @JvmSuppressWildcards Set<@JvmSuppressWildcards PaymentMethodType>>

    @Inject
    lateinit var applicationContext: Context

    internal val processing = AtomicBoolean(false)
    private var currentRequestId = -1

    private var startedNewTask = false

    var hostActivityProvider: ReplaySubject<AppCompatActivity> =
        ReplaySubject.create<AppCompatActivity>()

    lateinit var paymentMethodTypeSubject: ReplaySubject<PaymentMethodType>

    var errorSubject: PublishSubject<PaymentMethodAlias> = PublishSubject.create()

    var chooserUsed = false

    lateinit var currentChooserFragment: Fragment

    fun provideHostActivity(activity: AppCompatActivity) {
        hostActivityProvider.onNext(activity)
    }

    fun chooserCancelled() {
        hostActivityProvider = ReplaySubject.create()
        errorSubject.onError(RuntimeException())
        errorSubject = PublishSubject.create()
        processing.set(false)
        chooserUsed = false
    }

    fun entryCancelled() {
        hostActivityProvider = ReplaySubject.create()
        if (chooserUsed) {
            errorSubject.onError(EntryCancelled())
        } else {
            errorSubject.onError(UserCancelled())
        }
        errorSubject = PublishSubject.create()
        processing.set(false)
    }

    private fun flowCompleted(hostActivity: Activity) {
        if (startedNewTask) {
            hostActivity.finishAndRemoveTask()
        } else {
            hostActivity.finish()
        }

        hostActivityProvider = ReplaySubject.create()
        currentRequestId = -1
        processing.set(false)
    }

    internal fun availablePaymentMethods(): List<PaymentMethodType> {
        return integrations.values.flatMap {
            it.toList()
        }
    }

    /**
     * Request ID makes this reentrant from the perspective of PspCoordinators
     */
    fun checkFlow(requestId: Int) {
        if (processing.compareAndSet(false, true)) {
            if (currentRequestId != requestId && currentRequestId != -1) {
                throw RuntimeException("Already processing payment method entry")
            }
            currentRequestId = requestId
        } else {
            if (currentRequestId != requestId) {
                throw RuntimeException("Already processing payment method entry")
            }
        }
    }

    private fun launchHostActivity(activity: Activity?): Single<AppCompatActivity> {
        if (!hostActivityProvider.hasValue()) {
            if (activity != null) {
                startedNewTask = false
                val launchHostIntent =
                    Intent(activity, RegistrationProcessHostActivity::class.java)
                activity.startActivity(launchHostIntent)
            } else {
                startedNewTask = true
                val launchHostIntent =
                    Intent(applicationContext, RegistrationProcessHostActivity::class.java)
                launchHostIntent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
                applicationContext.startActivity(launchHostIntent)
            }
        }
        return hostActivityProvider.firstOrError()
    }

    fun handleCreditCardMethodEntryRequest(
        activity: Activity?,
        integration: Integration,
        paymentMethodType: PaymentMethodType,
        requestId: Int,
        block: (Pair<CreditCardData, AdditionalRegistrationData>) -> Single<PaymentMethodAlias>
    ): Single<PaymentMethodAlias> {
        checkFlow(requestId)
        val hostActivitySingle = launchHostActivity(activity)

        val resultSubject = PublishSubject.create<DataEntryResult>()

        return hostActivitySingle.flatMap { hostActivity ->
            (hostActivity as RegistrationProcessHostActivity).setState(
                RegistrationProcessHostActivity.CurrentState.ENTRY
            )
            integration.handlePaymentMethodEntryRequest(
                hostActivity,
                paymentMethodType,
                AdditionalRegistrationData(),
                resultSubject
            ).subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {

                    val localDate = LocalDate.parse(
                        it.extraData.getValue(CreditCardData.EXPIRY_DATE) + "/01",
                        DateTimeFormatter.ofPattern("MM/yy/dd")
                    )
                    val creditCardData = CreditCardData(
                        it.extraData.getValue(CreditCardData.CREDIT_CARD_NUMBER),
                        localDate.monthValue,
                        localDate.year,
                        it.extraData.getValue(CreditCardData.CVV),
                        BillingData(
                            firstName = BillingData.ADDITIONAL_DATA_FIRST_NAME,
                            lastName = it.extraData.getValue(BillingData.ADDITIONAL_DATA_LAST_NAME))
                    )
                    resultSubject.onNext(DataEntryResult.Processing())
                    block.invoke(Pair(creditCardData, it))
                        .toObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext {
                            resultSubject.onNext(DataEntryResult.Success())
                        }
                        .doOnError {
                            resultSubject.onNext(DataEntryResult.Failure(it))
                        }
                        .filterNotSuccess()
                }
                .firstOrError()
                .doFinally { flowCompleted(hostActivity) }
                .ambWith(errorSubject.firstOrError())
        }
    }

    fun handleSepaMethodEntryRequest(
        activity: Activity?,
        integration: Integration,
        paymentMethodType: PaymentMethodType,
        requestId: Int,
        block: (Pair<SepaData, AdditionalRegistrationData>) -> Single<PaymentMethodAlias>
    ): Single<PaymentMethodAlias> {

        checkFlow(requestId)

        val hostActivitySingle = launchHostActivity(activity)
        val resultSubject = PublishSubject.create<DataEntryResult>()

        return hostActivitySingle.flatMap { hostActivity ->
            (hostActivity as RegistrationProcessHostActivity).setState(
                RegistrationProcessHostActivity.CurrentState.ENTRY
            )
            integration.handlePaymentMethodEntryRequest(
                hostActivity,
                paymentMethodType,
                AdditionalRegistrationData(),
                resultSubject
            ).subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    val sepaData = SepaData(
                        iban = it.extraData.getValue(SepaData.IBAN),
                        billingData = BillingData(
                            firstName = it.extraData.getValue(BillingData.ADDITIONAL_DATA_FIRST_NAME),
                            lastName = it.extraData.getValue(BillingData.ADDITIONAL_DATA_LAST_NAME)
                        )
                    )
                    resultSubject.onNext(DataEntryResult.Processing())
                    block.invoke(Pair(sepaData, it))
                        .toObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext {
                            resultSubject.onNext(DataEntryResult.Success())
                        }
                        .doOnError {
                            resultSubject.onNext(DataEntryResult.Failure(it))
                        }
                        .filterNotSuccess()
                }.firstOrError()
                .doFinally { flowCompleted(hostActivity) }
                .ambWith(errorSubject.firstOrError())
        }
    }

    fun handlePaypalMethodEntryRequest(
        activity: Activity?,
        integration: Integration,
        additionalRegistrationData: AdditionalRegistrationData,
        requestId: Int
    ): Single<AdditionalRegistrationData> {
        checkFlow(requestId)
        val resultSubject = PublishSubject.create<DataEntryResult>()
        return launchHostActivity(activity).flatMap { hostActivity ->
            (hostActivity as RegistrationProcessHostActivity).setState(
                RegistrationProcessHostActivity.CurrentState.ENTRY
            )
            integration.handlePaymentMethodEntryRequest(
                hostActivity,
                PaymentMethodType.PAYPAL,
                additionalRegistrationData,
                resultSubject
            ).firstOrError()
                .doFinally {
                    flowCompleted(hostActivity)
                }.ambWith(errorSubject.map {
                    AdditionalRegistrationData()
                }.firstOrError())
        }
    }

    fun askUserToChosePaymentMethod(
        activity: Activity? = null,
        requestId: Int
    ): Single<PaymentMethodType> {
        checkFlow(requestId)
        chooserUsed = true
        return launchHostActivity(activity).flatMap { hostActivity ->
            val supportFragmentManager = hostActivity.supportFragmentManager
            (hostActivity as RegistrationProcessHostActivity).setState(
                RegistrationProcessHostActivity.CurrentState.CHOOSER
            )
            paymentMethodTypeSubject = ReplaySubject.create()
            val paymentMethodChoiceFragment = PaymentMethodChoiceFragment()
            currentChooserFragment = paymentMethodChoiceFragment
            supportFragmentManager.beginTransaction()
                .add(R.id.host_activity_fragment, paymentMethodChoiceFragment).commitNow()
            paymentMethodTypeSubject
                .doOnError {
                    flowCompleted(hostActivity)
                }
                .doOnNext {
                    supportFragmentManager.beginTransaction().remove(currentChooserFragment)
                        .commitNow()
                }.firstOrError()
        }
    }
}

private fun <T> Observable<T>.filterNotSuccess(): Observable<T> {
    return materialize()
        .filter {
            !it.isOnError && !it.isOnComplete
        }
        .map { it.value }
}