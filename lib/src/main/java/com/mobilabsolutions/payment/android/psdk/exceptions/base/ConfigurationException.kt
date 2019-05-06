package com.mobilabsolutions.payment.android.psdk.exceptions.base

class ConfigurationException(
    @Transient override val message: String = "Configuration Error",
    @Transient override val code: Int? = null,
    @Transient override val originalException: Throwable? = null
) : BasePaymentException(message, code, originalException)
