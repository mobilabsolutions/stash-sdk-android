package com.mobilabsolutions.payment.android.psdk.exceptions.base

class NetworkError(
    override val message: String = "Network Error",
    override val code: Int? = null,
    override val originalException: Throwable? = null
) : BasePaymentException(message, code, originalException)