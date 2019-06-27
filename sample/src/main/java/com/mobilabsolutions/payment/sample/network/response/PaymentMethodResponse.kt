package com.mobilabsolutions.payment.sample.network.response

import com.google.gson.annotations.SerializedName
import com.mobilabsolutions.payment.sample.network.CreditCardData
import com.mobilabsolutions.payment.sample.network.PayPalData
import com.mobilabsolutions.payment.sample.network.SepaData

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
data class PaymentMethodResponse(
    val paymentMethodId: String,
    @SerializedName("type") val type: String,
    @SerializedName("ccData") val creditCardData: CreditCardData?,
    @SerializedName("sepaData") val sepaData: SepaData?,
    @SerializedName("payPalData") val payPalData: PayPalData?
)