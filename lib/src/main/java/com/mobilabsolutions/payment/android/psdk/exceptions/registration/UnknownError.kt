package com.mobilabsolutions.payment.android.psdk.exceptions.registration

import com.mobilabsolutions.payment.android.psdk.exceptions.base.OtherException

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class UnknownError(
    override val message: String,
    override val code: Int? = null,
    override val originalException: Throwable? = null
) : OtherException(message, code, originalException)