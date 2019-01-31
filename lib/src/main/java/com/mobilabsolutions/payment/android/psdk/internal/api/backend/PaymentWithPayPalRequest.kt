package com.mobilabsolutions.payment.android.psdk.internal.api.backend

import com.mobilabsolutions.payment.android.psdk.model.BillingData

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class PaymentWithPayPalRequest(
        val amount: Int,
        val currency: String,
        val customerId: String?,
        val reason : String,
        val billingData : BillingData
)