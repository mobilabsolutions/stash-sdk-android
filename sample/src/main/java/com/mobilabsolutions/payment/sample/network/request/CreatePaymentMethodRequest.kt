package com.mobilabsolutions.payment.sample.network.request

import com.google.gson.annotations.SerializedName
import com.mobilabsolutions.payment.sample.network.CreditCardAliasData
import com.mobilabsolutions.payment.sample.network.PayPalAliasData
import com.mobilabsolutions.payment.sample.network.SepaAliasData

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
data class CreatePaymentMethodRequest(
    val aliasId: String,
    val type: String,
    val userId: String,
    @SerializedName("ccData") val creditCardAliasData: CreditCardAliasData? = null,
    @SerializedName("sepaData") val sepaAliasData: SepaAliasData? = null,
    @SerializedName("payPalData") val payPalAliasData: PayPalAliasData? = null
)