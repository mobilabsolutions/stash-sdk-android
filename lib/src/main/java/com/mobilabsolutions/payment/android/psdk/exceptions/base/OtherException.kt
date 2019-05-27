package com.mobilabsolutions.payment.android.psdk.exceptions.base

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
open class OtherException(
    @Transient override val message: String = "Other Exception",
    @Transient override val code: Int? = null,
    @Transient override val errorTitle : String? = null,
    @Transient override val originalException: Throwable? = null
) : BasePaymentException(message, errorTitle, code, originalException)