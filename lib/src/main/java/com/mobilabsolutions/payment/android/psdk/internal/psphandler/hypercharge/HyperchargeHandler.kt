package com.mobilabsolutions.payment.android.psdk.internal.psphandler.hypercharge

import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentSdk
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApi
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.PaymentMethodRegistrationResponse
import com.mobilabsolutions.payment.android.psdk.internal.api.hypercharge.HyperchargeApi
import com.mobilabsolutions.payment.android.psdk.internal.api.hypercharge.HyperchargeCreditCardPaymentRequest
import com.mobilabsolutions.payment.android.psdk.internal.api.hypercharge.HyperchargeSepaPaymentRequest
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.Single
import org.iban4j.CountryCode
import org.iban4j.Iban
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class HyperchargeHandler @Inject constructor(private val hyperchargeApi: HyperchargeApi, mobilabApi: MobilabApi) {
    private val TYPE_DEBIT_CARD = "direct_debit"
    private val TYPE_CREDIT_CARD = "credit_card"


    fun registerCreditCard(paymentMethodRegistrationResponse: PaymentMethodRegistrationResponse, creditCardData: CreditCardData): Single<String> {
        val hyperchargeCreditCardPaymentRequest = HyperchargeCreditCardPaymentRequest()
        hyperchargeCreditCardPaymentRequest.paymentMethod = TYPE_CREDIT_CARD
        hyperchargeCreditCardPaymentRequest.cardHolder = creditCardData.holder
        hyperchargeCreditCardPaymentRequest.cardNumber = creditCardData.number
        hyperchargeCreditCardPaymentRequest.cvv = creditCardData.cvv
        hyperchargeCreditCardPaymentRequest.expirationMonth = creditCardData.expiryDate?.monthValue.toString()
        hyperchargeCreditCardPaymentRequest.expirationYear = creditCardData.expiryDate?.year.toString()
        return hyperchargeApi.registerCreditCardAlias(
                paymentMethodRegistrationResponse.url!!,
                hyperchargeCreditCardPaymentRequest)
                .map { paymentMethodRegistrationResponse.paymentAlias }
    }

    fun registerSepa(paymentMethodRegistrationResponse: PaymentMethodRegistrationResponse, sepaData: SepaData): Single<String> {
        val hyperchargeSepaPaymentRequest = HyperchargeSepaPaymentRequest()
        hyperchargeSepaPaymentRequest.bankAccountHolder = sepaData.holder
        hyperchargeSepaPaymentRequest.paymentMethod = TYPE_DEBIT_CARD
        hyperchargeSepaPaymentRequest.bankNumber = sepaData.bankCodeFromIban
        hyperchargeSepaPaymentRequest.bankAccountNumber = sepaData.accountNumberFromIban
        return hyperchargeApi.registerSepaAlias(
                paymentMethodRegistrationResponse.url!!,
                hyperchargeSepaPaymentRequest)
                .map { paymentMethodRegistrationResponse.paymentAlias }
    }
}
