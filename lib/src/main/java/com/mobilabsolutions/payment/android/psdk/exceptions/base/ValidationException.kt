package com.mobilabsolutions.payment.android.psdk.exceptions.base

open class ValidationException(
    override val message: String,
    override val code: Int? = null,
    override val originalException: Throwable? = null
) : BasePaymentException(message, code, originalException)