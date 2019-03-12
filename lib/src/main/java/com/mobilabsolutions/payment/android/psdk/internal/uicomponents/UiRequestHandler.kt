package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.Single
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Singleton
class UiRequestHandler @Inject constructor() {


    fun handleCreditCardMethodEntryRequest(definition: PaymentMethodUiDefinition) : Pair<CreditCardData, Map<String, String>>{
            val validCreditCardData = CreditCardData(
                    "4111111111111111",
                    LocalDate.of(2021, 1, 1),
                    "123",
                    "Holder Holderman"
            )
        return Pair(validCreditCardData, mapOf("TEST" to "test"))
    }

    fun handleSepadMethodEntryRequest(definition: PaymentMethodUiDefinition) : Pair<SepaData, Map<String, String>> {
        var validSepaData: SepaData = SepaData("PBNKDEFF", "DE42721622981375897982", "Holder Holderman")
        return Pair(validSepaData, mapOf("TEST" to "test"))
    }

    fun hadlePaypalMethodEntryRequest(definition: PaymentMethodUiDefinition) : Pair<Any, Map<String, String>> {
        return Pair("", mapOf("TEST" to "test"))
    }

    fun askUserToChosePaymentMethod(availableMethods : List<PaymentMethodType>) : Single<PaymentMethodType> {
        return Single.just(PaymentMethodType.CREDITCARD)
    }

}