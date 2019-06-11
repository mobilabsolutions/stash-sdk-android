package com.mobilabsolutions.payment.android.psdk.internal

import com.mobilabsolutions.payment.android.psdk.PaymentMethodAlias
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType

data class IdempotencyData(
    val timestamp: Long,
    val paymentMethodType: PaymentMethodType?,
    val paymentMethodAlias: PaymentMethodAlias?,
    val error: Throwable?
)