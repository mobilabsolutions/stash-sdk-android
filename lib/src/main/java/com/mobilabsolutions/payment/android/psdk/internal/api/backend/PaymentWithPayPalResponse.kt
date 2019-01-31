package com.mobilabsolutions.payment.android.psdk.internal.api.backend

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class PaymentWithPayPalResponse(
        val mappedTransactionId : String,
        val redirectUrl : String
)