package com.mobilabsolutions.payment.sample.network.request

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
data class CreatePaymentMethodRequest(
    val alias: String,
    val aliasId: String,
    val type: String,
    val userId: String
)