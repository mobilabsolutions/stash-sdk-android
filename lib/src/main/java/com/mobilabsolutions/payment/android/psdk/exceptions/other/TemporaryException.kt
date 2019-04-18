package com.mobilabsolutions.payment.android.psdk.exceptions.other

import com.mobilabsolutions.payment.android.psdk.exceptions.ProviderOriginatedException

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class TemporaryException(
    override val message: String = "A temporary error has been reported by payment provider, please try again later",
    val providerMessage: String = "There was no specific message from payment provider supplied"
) : ProviderOriginatedException(CODE, message) {
    companion object {
        @JvmStatic
        val CODE = 1234
    }
}
