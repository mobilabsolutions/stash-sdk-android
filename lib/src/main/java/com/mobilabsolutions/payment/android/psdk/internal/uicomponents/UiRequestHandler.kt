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

    lateinit var hostActivityProvider: ReplaySubject<AppCompatActivity>

    lateinit var paymentMethodTypeSubject: PublishSubject<PaymentMethodType>

    fun provideHostActivity(activity: AppCompatActivity) {
        hostActivityProvider.onNext(activity)
    }

    private fun flowCompleted(hostActivity: Activity) {
        hostActivity.finish()
        hostActivityProvider = ReplaySubject.create()
        processing.set(false)
    }

    fun checkFlow() {
        if (processing.compareAndSet(false, true)) {
            // Do nothing, we can proceed
        } else {
            throw RuntimeException("Already processing payment method entry")
        }
    }

    private fun launchHostActivity(activity: Activity?): Single<AppCompatActivity> {
        hostActivityProvider = ReplaySubject.create<AppCompatActivity>()
        if (activity != null) {
            val launchHostIntent = Intent(activity, RegistrationProccessHostActivity::class.java)
            activity.startActivity(launchHostIntent)
        } else {
            val launchHostIntent = Intent(applicationContext, RegistrationProccessHostActivity::class.java)
            launchHostIntent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
            applicationContext.startActivity(launchHostIntent)
        }
        return hostActivityProvider.firstOrError()

    }


    fun handleCreditCardMethodEntryRequest(activity: Activity?, integration: Integration, definition: PaymentMethodDefinition): Single<Pair<CreditCardData, Map<String, String>>> {
        checkFlow()
        val hostActivitySingle = launchHostActivity(activity)
        return hostActivitySingle.flatMap { hostActivity ->
            integration.handlePaymentMethodEntryRequest(hostActivity, definition)
                    .doFinally {
                        flowCompleted(hostActivity)
                    }
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

    fun handleSepaMethodEntryRequest(activity: Activity?, integration: Integration, definition: PaymentMethodDefinition): Single<Pair<SepaData, Map<String, String>>> {
        checkFlow()
        val hostActivitySingle = launchHostActivity(activity)
        var validSepaData: SepaData = SepaData("PBNKDEFF", "DE42721622981375897982", "Holder Holderman")
        return return hostActivitySingle.flatMap { hostActivity ->
            integration.handlePaymentMethodEntryRequest(hostActivity, definition)
                    .doFinally {
                        flowCompleted(hostActivity)
                    }
        }.map {
            Pair(validSepaData, mapOf("TEST" to "test"))
        }
    }

    fun handlePaypalMethodEntryRequest(activity: Activity?, integration: Integration, definition: PaymentMethodDefinition): Single<Map<String, String>> {
        checkFlow()
        return launchHostActivity(activity).flatMap { hostActivity ->
            integration.handlePaymentMethodEntryRequest(hostActivity, PaymentMethodDefinition("", "BRAINTREE", PaymentMethodType.PAYPAL))
                    .doFinally {
                        flowCompleted(hostActivity)
                    }
        }
    }

    fun askUserToChosePaymentMethod(activity: Activity?, availableMethods: Set<PaymentMethodType>): Single<PaymentMethodType> {
        return launchHostActivity(activity).flatMap { hostActivity ->
            val supportFragmentManager = hostActivity.supportFragmentManager
            paymentMethodTypeSubject = PublishSubject.create()
            val paymentMethodChoiceFragment = PaymentMethodChoiceFragment()
            supportFragmentManager.beginTransaction().add(R.id.host_activity_fragment, paymentMethodChoiceFragment).commitNow()
            paymentMethodTypeSubject.doOnNext {
                supportFragmentManager.beginTransaction().remove(paymentMethodChoiceFragment).commitNow()
            }.firstOrError()
        }

    }

}