package com.mobilabsolutions.payment.sample.data.mappers

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
interface Mapper<F, T> {
    fun map(from: F): T
}