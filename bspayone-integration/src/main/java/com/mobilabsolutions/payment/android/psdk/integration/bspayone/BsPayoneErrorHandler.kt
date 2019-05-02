package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import com.mobilabsolutions.payment.android.psdk.exceptions.base.OtherException
import com.mobilabsolutions.payment.android.psdk.exceptions.registration.RegistrationFailedException
import com.mobilabsolutions.payment.android.psdk.exceptions.registration.UnknownError
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.pspapi.BsPayoneVerificationErrorResponse
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.pspapi.BsPayoneVerificationInvalidResponse

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
object BsPayoneErrorHandler {
    fun handleError(error: BsPayoneVerificationErrorResponse): Throwable {
        return when (error.errorCode) {
            7, 43, 56, 62, 880 -> RegistrationFailedException(providerMessage = error.customerMessage
                ?: "No custom message provided")
            else -> UnknownError("Unknown error, code from provider ${error.errorCode}", error.errorCode, OtherException(error.customerMessage
                ?: "No custom message provided", error.errorCode))
        }
    }

    fun handleError(error: BsPayoneVerificationInvalidResponse): Throwable {
        return when (error.errorCode) {
            14 -> RegistrationFailedException(providerMessage = error.customerMessage
                ?: "No custom message provided")
            else -> UnknownError("Unknown error, code from provider ${error.errorCode}", error.errorCode, OtherException(error.customerMessage
                ?: "No custom message provided", error.errorCode))
        }
    }
}