package com.mobilabsolutions.payment.sample.data

import com.mobilabsolutions.payment.sample.data.entities.ErrorResult
import com.mobilabsolutions.payment.sample.data.entities.Result
import com.mobilabsolutions.payment.sample.data.entities.Success
import com.mobilabsolutions.payment.sample.data.mappers.Mapper
import com.mobilabsolutions.payment.sample.extensions.bodyOrThrow
import com.mobilabsolutions.payment.sample.extensions.toException
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-04-2019.
 */
@Singleton
class RetrofitRunner @Inject constructor() {
    suspend fun <T, E> executeForResponse(mapper: Mapper<T, E>, request: suspend () -> Response<T>): Result<E> {
        return try {
            val response = request()
            if (response.isSuccessful) {
                Success(data = mapper.map(response.bodyOrThrow()))
            } else {
                ErrorResult(response.toException())
            }
        } catch (e: Exception) {
            ErrorResult(e)
        }
    }

    suspend fun <T> executeForResponse(request: suspend () -> Response<T>): Result<Unit> {
        val unitMapper = object : Mapper<T, Unit> {
            override suspend fun map(from: T) = Unit
        }
        return executeForResponse(unitMapper, request)
    }
}