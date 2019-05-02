package com.mobilabsolutions.payment.android.psdk.exceptions.base

class ConfigurationError(
    override val message: String = "Configuration Error",
    override val code: Int? = null,
    override val originalException: Throwable? = null
) : BasePaymentException(message, code, originalException)
