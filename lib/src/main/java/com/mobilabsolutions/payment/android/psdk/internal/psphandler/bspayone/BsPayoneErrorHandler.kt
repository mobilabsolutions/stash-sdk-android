package com.mobilabsolutions.payment.android.psdk.internal.psphandler.bspayone

import com.mobilabsolutions.payment.android.psdk.exceptions.other.UnknownError
import com.mobilabsolutions.payment.android.psdk.exceptions.registration.CreditCardRegistrationException
import com.mobilabsolutions.payment.android.psdk.internal.api.payone.BsPayoneVerificationErrorResponse
import com.mobilabsolutions.payment.android.psdk.internal.api.payone.BsPayoneVerificationInvalidResponse

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
object BsPayoneErrorHandler {
    fun handleError(error : BsPayoneVerificationErrorResponse) : Throwable {
        return when(error.errorCode) {
            7, 43, 56, 62, 880 -> CreditCardRegistrationException(providerMessage = error.customerMessage ?: "No custom message provided")
            else -> UnknownError(message = "Unknown error, code from provider ${error.errorCode}", providerMessage = error.customerMessage ?: "No custom message provided")
        }
    }

    fun handleError(error : BsPayoneVerificationInvalidResponse) : Throwable {
        return when(error.errorCode) {
            14 -> CreditCardRegistrationException(providerMessage = error.customerMessage ?: "No custom message provided")
            else -> UnknownError(message = "Unknown error, invalid card number designation, code from provider ${error.errorCode}", providerMessage = error.customerMessage ?: "No custom message provided")
        }
    }
}