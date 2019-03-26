package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.app.Activity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.Single
import org.threeten.bp.LocalDate
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Singleton
class UiRequestHandler @Inject constructor() {

    @Inject
    lateinit var integrations: List<Integration>

    lateinit var activityReference : WeakReference<Activity>


    fun handleCreditCardMethodEntryRequest(definition: PaymentMethodDefinition): Pair<CreditCardData, Map<String, String>> {
        val validCreditCardData = CreditCardData(
                "4111111111111111",
                LocalDate.of(2021, 1, 1),
                "123",
                "Holder Holderman"
        )
        return Pair(validCreditCardData, mapOf("TEST" to "test"))
    }

    fun handleSepadMethodEntryRequest(definition: PaymentMethodDefinition): Pair<SepaData, Map<String, String>> {
        var validSepaData: SepaData = SepaData("PBNKDEFF", "DE42721622981375897982", "Holder Holderman")
        return Pair(validSepaData, mapOf("TEST" to "test"))
    }

    fun hadlePaypalMethodEntryRequest(activity: Activity?, registrationRequest: RegistrationRequest): Single<String> {
        if (activity != null) {
            return integrations[0].handlePaymentMethodEntryRequest(activity, registrationRequest)
        } else {
            return integrations[0].handlePaymentMethodEntryRequest(activityReference.get()!!, registrationRequest)
        }

    }

    fun askUserToChosePaymentMethod(activity: Activity?, availableMethods: Set<PaymentMethodType>): Single<PaymentMethodType> {
        return Single.just(PaymentMethodType.PAYPAL)
    }

}