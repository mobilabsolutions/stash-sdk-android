package com.mobilabsolutions.payment.sample.state

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class PaymentMethodState(
    val paymentMethodMap: Map<String, String> = HashMap()
)
