package com.mobilabsolutions.payment.sample.network.response

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
data class PaymentMethodResponse(
    val paymentMethodId: String,
    val alias: String,
    val type: String
)