package com.mobilabsolutions.payment.android.psdk.exceptions.base

open class ValidationException(
    override val message: String = "Validation Exception",
    override val code: Int? = null,
    override val errorTitle: String? = null,
    override val originalException: Throwable? = null
) : BasePaymentException(message, errorTitle, code, originalException)