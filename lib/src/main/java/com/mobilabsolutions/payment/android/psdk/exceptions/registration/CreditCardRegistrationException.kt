package com.mobilabsolutions.payment.android.psdk.exceptions.registration

import com.mobilabsolutions.payment.android.psdk.exceptions.ProviderOriginatedException

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class CreditCardRegistrationException(
    override val message: String = "Card registration failed",
    val providerMessage: String = "There was no specific message from payment provider supplied"
) : ProviderOriginatedException(CODE, message) {
    companion object {
        @JvmStatic
        val CODE = 1234
    }
}