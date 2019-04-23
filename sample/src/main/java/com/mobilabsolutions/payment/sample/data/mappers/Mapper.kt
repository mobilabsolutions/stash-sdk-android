package com.mobilabsolutions.payment.sample.data.mappers

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-04-2019.
 */
interface Mapper<F, T> {
    suspend fun map(from: F): T
}