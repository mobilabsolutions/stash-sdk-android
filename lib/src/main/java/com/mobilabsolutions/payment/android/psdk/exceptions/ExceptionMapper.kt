package com.mobilabsolutions.payment.android.psdk.exceptions

import com.google.gson.Gson
import com.mobilabsolutions.payment.android.psdk.exceptions.base.BasePaymentException
import com.mobilabsolutions.payment.android.psdk.exceptions.base.OtherException
import com.mobilabsolutions.payment.android.psdk.exceptions.base.TemporaryException
import com.mobilabsolutions.payment.android.psdk.exceptions.payment.PaymentFailedException
import com.mobilabsolutions.payment.android.psdk.exceptions.registration.RegistrationFailedException
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.ErrorResponse
import retrofit2.HttpException

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class ExceptionMapper(val gson: Gson) {
    fun mapError(httpException: HttpException): BasePaymentException {
        val errorBody = httpException.response().errorBody()?.string()
        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
        return when (httpException.code()) {
            701, 702 -> RegistrationFailedException(errorResponse.error.message, errorResponse.error.code)
            703 -> PaymentFailedException(errorResponse.error.message, errorResponse.error.code)
            705 -> TemporaryException(errorResponse.error.message, errorResponse.error.code)
            else -> OtherException(errorResponse.error.message, errorResponse.error.code)
        }
    }
}