package com.mobilabsolutions.payment.android.psdk.exceptions.payment

import com.mobilabsolutions.payment.android.psdk.exceptions.base.BasePaymentException

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PaymentFailedException(
    override val message: String = "Payment failed",
    override val code: Int? = null,
    override val errorTitle: String? = null,
    override val originalException: Throwable? = null
) : BasePaymentException(message, errorTitle, code)