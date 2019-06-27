package com.mobilabsolutions.payment.sample.network.request

import com.google.gson.annotations.SerializedName
import com.mobilabsolutions.payment.sample.network.CreditCardData
import com.mobilabsolutions.payment.sample.network.PayPalData
import com.mobilabsolutions.payment.sample.network.SepaData

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
data class CreatePaymentMethodRequest(
    val aliasId: String,
    val type: String,
    val userId: String,
    @SerializedName("ccData") val creditCardData: CreditCardData? = null,
    @SerializedName("sepaData") val sepaData: SepaData? = null,
    @SerializedName("payPalData") val payPalData: PayPalData? = null
)