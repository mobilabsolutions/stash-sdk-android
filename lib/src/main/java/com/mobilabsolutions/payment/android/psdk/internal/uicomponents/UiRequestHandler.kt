package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
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

    lateinit var activityReference: WeakReference<Activity>

    internal val processing = AtomicBoolean(false)

    lateinit var hostActivityProvider: PublishSubject<AppCompatActivity>

    fun provideHostActivity(activity: AppCompatActivity) {
        activityReference = WeakReference(activity)
        hostActivityProvider.onNext(activity)
    }

    private fun launchHostActivity(activity: Activity?): Single<AppCompatActivity> {
        if (activity != null) {
            val launchHostIntent = Intent(activity, RegistrationProccessHostActivity::class.java)
            activity.startActivity(launchHostIntent)
        } else {
            val launchHostIntent = Intent(applicationContext, RegistrationProccessHostActivity::class.java)
            launchHostIntent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
            applicationContext.startActivity(launchHostIntent)
        }
        hostActivityProvider = PublishSubject.create<AppCompatActivity>()
        return hostActivityProvider.firstOrError()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun handleCreditCardMethodEntryRequest(activity: Activity?, integration: Integration, definition: PaymentMethodDefinition): Single<Pair<CreditCardData, Map<String, String>>> {
        val hostActivitySingle = launchHostActivity(activity)
        return hostActivitySingle.flatMap { hostActivity ->
            integration.handlePaymentMethodEntryRequest(hostActivity, definition)
                    .doFinally {
                        hostActivity.finish()
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
        var validSepaData: SepaData = SepaData("PBNKDEFF", "DE42721622981375897982", "Holder Holderman")
        return Single.just(Pair(validSepaData, mapOf("TEST" to "test")))
    }

    fun handlePaypalMethodEntryRequest(activity: Activity?, integration: Integration, definition: PaymentMethodDefinition): Single<Map<String, String>> {
        return launchHostActivity(activity).flatMap { hostActivity ->
            integration.handlePaymentMethodEntryRequest(hostActivity, PaymentMethodDefinition("", "BRAINTREE", PaymentMethodType.PAYPAL))
                    .doFinally {
                        hostActivity.finish()
                    }
        }
    }

    fun askUserToChosePaymentMethod(activity: Activity?, availableMethods: Set<PaymentMethodType>): Single<PaymentMethodType> {
        return Single.just(PaymentMethodType.CREDITCARD)
    }

}