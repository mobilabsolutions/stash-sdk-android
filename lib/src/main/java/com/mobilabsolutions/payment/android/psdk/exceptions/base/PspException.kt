package com.mobilabsolutions.payment.android.psdk.exceptions.base

class PspException(
    @Transient override val message: String = "PSP error",
    @Transient override val code: Int? = null,
    @Transient override val errorTitle : String? = null,
    @Transient override val originalException: Throwable? = null
) : BasePaymentException(message, errorTitle, code, originalException)