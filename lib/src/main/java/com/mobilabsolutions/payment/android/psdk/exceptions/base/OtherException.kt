package com.mobilabsolutions.payment.android.psdk.exceptions.base

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
open class OtherException(
    override val message: String = "Other Exception",
    override val code: Int? = null,
    override val originalException: Throwable? = null
) : BasePaymentException(message, code, originalException)