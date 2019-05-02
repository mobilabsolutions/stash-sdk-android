package com.mobilabsolutions.payment.android.psdk.exceptions.base

class UserActionableError(
    override val message: String,
    override val code: Int? = null,
    override val originalException: Throwable? = null
) : BasePaymentException(message, code, originalException)
