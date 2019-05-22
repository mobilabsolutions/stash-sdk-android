package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mobilabsolutions.payment.android.R
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
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

    class EntryCancelled : RuntimeException()
    class UserCancelled : RuntimeException("User cancelled")

    @Inject
    lateinit var integrations: Set<@JvmSuppressWildcards Integration>

    @Inject
    lateinit var applicationContext: Context

    internal val processing = AtomicBoolean(false)
    private var currentRequestId = -1

    private var startedNewTask = false

    var hostActivityProvider: ReplaySubject<AppCompatActivity> =
            ReplaySubject.create<AppCompatActivity>()

    lateinit var paymentMethodTypeSubject: ReplaySubject<PaymentMethodType>

    var errorSubject: PublishSubject<Map<String, String>> = PublishSubject.create()

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

    internal fun availablePaymentMethods(): List<PaymentMethodDefinition> {
        return integrations.flatMap { it.getSupportedPaymentMethodDefinitions() }
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
        definition: PaymentMethodDefinition,
        requestId: Int
    ): Single<Pair<CreditCardData, Map<String, String>>> {
        checkFlow(requestId)
        val hostActivitySingle = launchHostActivity(activity)
        return hostActivitySingle.flatMap { hostActivity ->
            (hostActivity as RegistrationProcessHostActivity).setState(
                    RegistrationProcessHostActivity.CurrentState.ENTRY
            )
            integration.handlePaymentMethodEntryRequest(hostActivity, definition, AdditionalRegistrationData())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        flowCompleted(hostActivity)
                    }.ambWith(errorSubject.firstOrError())
        }.map {
            val validCreditCardData = CreditCardData(
                    it.getValue(CreditCardData.CREDIT_CARD_NUMBER),
                    LocalDate.parse(
                            it.getValue(CreditCardData.EXPIRY_DATE) + "/01",
                            DateTimeFormatter.ofPattern("MM/yy/dd")
                    ),
                    it.getValue(CreditCardData.CVV),
                    it.getValue(BillingData.FIRST_NAME) + " " + it.getValue(BillingData.LAST_NAME)
            )
            val additionalDataMap: Map<String, String> = it

            Pair(validCreditCardData, additionalDataMap)
        }
    }

    fun handleSepaMethodEntryRequest(
        activity: Activity?,
        integration: Integration,
        definition: PaymentMethodDefinition,
        requestId: Int
    ): Single<Pair<SepaData, Map<String, String>>> {
        checkFlow(requestId)
        val hostActivitySingle = launchHostActivity(activity)
        return hostActivitySingle.flatMap { hostActivity ->
            (hostActivity as RegistrationProcessHostActivity).setState(
                    RegistrationProcessHostActivity.CurrentState.ENTRY
            )
            integration.handlePaymentMethodEntryRequest(hostActivity, definition, AdditionalRegistrationData())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        flowCompleted(hostActivity)
                    }.ambWith(errorSubject.firstOrError())
        }.map {
            val sepaData = SepaData(
                    iban = it.getValue(SepaData.IBAN),
                    holder = it.getValue(SepaData.FIRST_NAME) + " " + it.getValue(SepaData.LAST_NAME)
            )

            Pair(sepaData, it)
        }
    }

    fun handlePaypalMethodEntryRequest(
        activity: Activity?,
        integration: Integration,
        additionalRegistrationData: AdditionalRegistrationData,
        requestId: Int
    ): Single<Map<String, String>> {
        checkFlow(requestId)
        return launchHostActivity(activity).flatMap { hostActivity ->
            (hostActivity as RegistrationProcessHostActivity).setState(
                    RegistrationProcessHostActivity.CurrentState.ENTRY
            )
            integration.handlePaymentMethodEntryRequest(
                    hostActivity,
                    PaymentMethodDefinition("", "BRAINTREE", PaymentMethodType.PAYPAL),
                    additionalRegistrationData
            )
                    .doFinally {
                        flowCompleted(hostActivity)
                    }.ambWith(errorSubject.firstOrError())
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