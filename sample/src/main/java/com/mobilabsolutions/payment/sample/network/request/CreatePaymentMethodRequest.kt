package com.mobilabsolutions.payment.sample.network.request

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
data class CreatePaymentMethodRequest(
    val aliasId: String,
    val alias: String,
    val type: String,
    val userId: String
)