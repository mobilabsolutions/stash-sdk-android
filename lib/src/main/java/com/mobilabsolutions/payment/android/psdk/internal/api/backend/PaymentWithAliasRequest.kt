package com.mobilabsolutions.payment.android.psdk.internal.api.backend

import java.util.*

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class PaymentWithAliasRequest(
        val amount: Int,
        val currency: String,
        val customerId: String?,
        val paymentAlias :String,
        val reason : String
)