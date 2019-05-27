package com.mobilabsolutions.payment.android.psdk.exceptions.registration

import com.mobilabsolutions.payment.android.psdk.exceptions.base.OtherException

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class UnknownException(
    @Transient override val message: String = "Unknown Exception",
    @Transient override val code: Int? = null,
    @Transient override val errorTitle: String? = null,
    @Transient override val originalException: Throwable? = null
) : OtherException(message, code, errorTitle, originalException)