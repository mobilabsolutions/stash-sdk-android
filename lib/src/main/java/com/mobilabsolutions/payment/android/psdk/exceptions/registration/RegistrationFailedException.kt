package com.mobilabsolutions.payment.android.psdk.exceptions.registration

import com.mobilabsolutions.payment.android.psdk.exceptions.base.BasePaymentException

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class RegistrationFailedException(
    @Transient override val message: String = "Registration failed",
    @Transient override val code: Int? = null,
    @Transient override val originalException: Throwable? = null
) : BasePaymentException(message, code)