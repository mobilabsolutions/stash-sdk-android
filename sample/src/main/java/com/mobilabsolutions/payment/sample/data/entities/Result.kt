package com.mobilabsolutions.payment.sample.data.entities

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-04-2019.
 */

sealed class Result<T> {
    open fun get(): T? = null
}

data class Success<T>(val data: T) : Result<T>() {
    override fun get(): T = data
}

data class ErrorResult<T>(val exception: Exception) : Result<T>()