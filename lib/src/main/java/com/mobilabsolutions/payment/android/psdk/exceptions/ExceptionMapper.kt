package com.mobilabsolutions.payment.android.psdk.exceptions

import com.google.gson.Gson
import com.mobilabsolutions.payment.android.psdk.exceptions.base.AuthenticationException
import com.mobilabsolutions.payment.android.psdk.exceptions.base.BasePaymentException
import com.mobilabsolutions.payment.android.psdk.exceptions.base.ConfigurationException
import com.mobilabsolutions.payment.android.psdk.exceptions.base.NetworkException
import com.mobilabsolutions.payment.android.psdk.exceptions.base.OtherException
import com.mobilabsolutions.payment.android.psdk.exceptions.base.ValidationException
import com.mobilabsolutions.payment.android.psdk.exceptions.registration.UnknownException
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
            in 1000..1003 -> AuthenticationException(errorResponse.message, errorResponse.code)
            in 2000..2006 -> ValidationException(errorResponse.message, errorResponse.code)
            in 3000..3017 -> ConfigurationException(errorResponse.message, errorResponse.code)
            4000 -> NetworkException(errorResponse.message, errorResponse.code)
            5000 -> OtherException(errorResponse.message, errorResponse.code)
            else -> UnknownException(errorResponse.message, errorResponse.code)
        }
    }
}