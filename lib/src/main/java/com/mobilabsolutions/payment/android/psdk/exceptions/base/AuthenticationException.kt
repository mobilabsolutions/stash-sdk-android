package com.mobilabsolutions.payment.android.psdk.exceptions.base

class AuthenticationException(
    override val message: String = "Authentication Failed",
    override val code: Int? = null,
    override val errorTitle: String? = null,
    override val originalException: Throwable? = null
) : BasePaymentException(message, errorTitle, code, originalException)
