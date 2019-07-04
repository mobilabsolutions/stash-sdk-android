package com.mobilabsolutions.payment.android.psdk.exceptions.base

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class TemporaryException(
    override val message: String = "A temporary error has been reported by payment provider, please try again later",
    override val code: Int? = null,
    override val errorTitle: String? = null,
    override val originalException: Throwable? = null
) : BasePaymentException(message, errorTitle, code, originalException)
