package com.mobilabsolutions.payment.android.psdk.exceptions

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
open class ProviderOriginatedException(val code: Int, override val message: String) : RuntimeException(message)