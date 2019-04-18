package com.mobilabsolutions.payment.android.psdk.exceptions.backend

import com.google.gson.Gson
import com.mobilabsolutions.payment.android.psdk.exceptions.other.TemporaryException
import com.mobilabsolutions.payment.android.psdk.exceptions.payment.PaymentFailedException
import com.mobilabsolutions.payment.android.psdk.exceptions.registration.CreditCardRegistrationException
import com.mobilabsolutions.payment.android.psdk.exceptions.registration.SepaRegistrationFailed
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.ErrorResponse
import retrofit2.HttpException

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BackendExceptionMapper(val gson: Gson) {
    val noProviderMessage = "There was no specific message from payment provider supplied"
    fun mapError(httpException: HttpException): RuntimeException {
        val errorBody = httpException.response().errorBody()?.string()
        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
        return when (httpException.code()) {
            701 -> CreditCardRegistrationException(errorResponse.error.message, errorResponse.error.providerDetails ?: noProviderMessage)
            702 -> SepaRegistrationFailed(errorResponse.error.message, errorResponse.error.providerDetails ?: noProviderMessage)
            703 -> PaymentFailedException(errorResponse.error.message, errorResponse.error.providerDetails ?: noProviderMessage)
            705 -> TemporaryException(errorResponse.error.message, errorResponse.error.providerDetails ?: noProviderMessage)
            else -> UnknownBackendException(errorResponse.error.message, errorResponse.error.providerDetails ?: noProviderMessage)
        }
    }
}