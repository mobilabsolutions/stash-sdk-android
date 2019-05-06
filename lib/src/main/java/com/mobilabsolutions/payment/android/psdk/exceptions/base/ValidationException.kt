package com.mobilabsolutions.payment.android.psdk.exceptions.base

open class ValidationException(
    @Transient override val message: String = "Validation Exception",
    @Transient override val code: Int? = null,
    @Transient override val originalException: Throwable? = null
) : BasePaymentException(message, code, originalException)