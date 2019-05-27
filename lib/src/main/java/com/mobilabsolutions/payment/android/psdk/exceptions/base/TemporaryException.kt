package com.mobilabsolutions.payment.android.psdk.exceptions.base

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class TemporaryException(
    @Transient override val message: String = "A temporary error has been reported by payment provider, please try again later",
    @Transient override val code: Int? = null,
    @Transient override val errorTitle : String? = null,
    @Transient override val originalException: Throwable? = null
) : BasePaymentException(message, errorTitle, code, originalException)
