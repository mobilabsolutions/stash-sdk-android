package com.mobilabsolutions.payment.android.psdk.internal.api.backend

import java.util.*

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class PaymentWithAliasResponse(
        val amount: Int,
        val currency: String,
        val customerId: String?,
        val reason : String,
        val status : String,
        val timestamp : String, //TODO add threeten converter
        val transactionId : String
)