package com.mobilabsolutions.payment.android.psdk.internal.api.backend

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class PayPalConfirmationRequest(
        val mappedTransactionId : String,
        val status : String,
        val code : String
)