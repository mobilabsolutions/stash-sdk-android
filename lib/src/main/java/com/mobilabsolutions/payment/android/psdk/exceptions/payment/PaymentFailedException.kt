package com.mobilabsolutions.payment.android.psdk.exceptions.payment

import com.mobilabsolutions.payment.android.psdk.exceptions.base.BasePaymentException

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PaymentFailedException(
    @Transient override val message: String = "Payment failed",
    @Transient override val code: Int? = null,
    @Transient override val originalException: Throwable? = null
) : BasePaymentException(message, code)