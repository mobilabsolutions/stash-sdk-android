package com.mobilabsolutions.payment.android.psdk.exceptions.base

class AuthenticationException(
    @Transient override val message: String = "Authentication Failed",
    @Transient override val code: Int? = null,
    @Transient override val originalException: Throwable? = null
) : BasePaymentException(message, code, originalException)
