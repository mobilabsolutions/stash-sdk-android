package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.R
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import org.threeten.bp.LocalDate
import timber.log.Timber
import java.lang.RuntimeException
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Singleton
class UiRequestHandler @Inject constructor() {

    @Inject
    lateinit var integrations: Set<@JvmSuppressWildcards Integration>

    @Inject
    lateinit var applicationContext: Context

    internal val processing = AtomicBoolean(false)
    private var currentRequestId = -1

    private var startedNewTask = false

    var hostActivityProvider: ReplaySubject<AppCompatActivity> = ReplaySubject.create<AppCompatActivity>()

    lateinit var paymentMethodTypeSubject: PublishSubject<PaymentMethodType>

    var errorSubject : PublishSubject<Map<String, String>> = PublishSubject.create()

    fun provideHostActivity(activity: AppCompatActivity) {
        hostActivityProvider.onNext(activity)
    }

    fun hostActivityDismissed() {
        hostActivityProvider = ReplaySubject.create()
        errorSubject.onError(RuntimeException("User cancelled"))
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

    /**
     * Request ID makes this reentrant from the perspective of PspCoordinators
     */
    fun checkFlow(requestId : Int) {
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
                val launchHostIntent = Intent(activity, RegistrationProccessHostActivity::class.java)
                activity.startActivity(launchHostIntent)
            } else {
                startedNewTask = true
                val launchHostIntent = Intent(applicationContext, RegistrationProccessHostActivity::class.java)
                launchHostIntent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
                applicationContext.startActivity(launchHostIntent)
            }
        }
        return hostActivityProvider.firstOrError()

    }


    fun handleCreditCardMethodEntryRequest(activity: Activity?, integration: Integration, definition: PaymentMethodDefinition, requestId: Int): Single<Pair<CreditCardData, Map<String, String>>> {
        checkFlow(requestId)
        val hostActivitySingle = launchHostActivity(activity)
        return hostActivitySingle.flatMap { hostActivity ->
            integration.handlePaymentMethodEntryRequest(hostActivity, definition)
                    .doFinally {
                        flowCompleted(hostActivity)
                    }.ambWith(errorSubject.firstOrError())
        }.map {
            val validCreditCardData = CreditCardData(
                    "4111111111111111",
                    LocalDate.of(2021, 1, 1),
                    "123",
                    "Holder Holderman"
            )

            Pair(validCreditCardData, mapOf("TEST" to "test"))
        }


    }

    fun handleSepaMethodEntryRequest(activity: Activity?, integration: Integration, definition: PaymentMethodDefinition, requestId: Int): Single<Pair<SepaData, Map<String, String>>> {
        checkFlow(requestId)
        val hostActivitySingle = launchHostActivity(activity)
        var validSepaData = SepaData("PBNKDEFF", "DE42721622981375897982", "Holder Holderman")
        return hostActivitySingle.flatMap { hostActivity ->
            integration.handlePaymentMethodEntryRequest(hostActivity, definition)
                    .doFinally {
                        flowCompleted(hostActivity)
                    }.ambWith(errorSubject.firstOrError())
        }.map {
            Pair(validSepaData, mapOf("TEST" to "test"))
        }
    }

    fun handlePaypalMethodEntryRequest(activity: Activity?, integration: Integration, definition: PaymentMethodDefinition, requestId: Int): Single<Map<String, String>> {
        checkFlow(requestId)
        return launchHostActivity(activity).flatMap { hostActivity ->
            integration.handlePaymentMethodEntryRequest(hostActivity, PaymentMethodDefinition("", "BRAINTREE", PaymentMethodType.PAYPAL))
                    .doFinally {
                        flowCompleted(hostActivity)
                    }.ambWith(errorSubject.firstOrError())
        }
    }

    fun askUserToChosePaymentMethod(activity: Activity?, availableMethods: Set<PaymentMethodType>, requestId: Int): Single<PaymentMethodType> {
        checkFlow(requestId)
        return launchHostActivity(activity).flatMap { hostActivity ->
            val supportFragmentManager = hostActivity.supportFragmentManager
            paymentMethodTypeSubject = PublishSubject.create()
            val paymentMethodChoiceFragment = PaymentMethodChoiceFragment()
            supportFragmentManager.beginTransaction().add(R.id.host_activity_fragment, paymentMethodChoiceFragment).commitNow()
            paymentMethodTypeSubject
                    .doOnError {
                        flowCompleted(hostActivity)
                    }
                    .doOnNext {
                        supportFragmentManager.beginTransaction().remove(paymentMethodChoiceFragment).commitNow()
                    }.firstOrError()
        }

    }

}