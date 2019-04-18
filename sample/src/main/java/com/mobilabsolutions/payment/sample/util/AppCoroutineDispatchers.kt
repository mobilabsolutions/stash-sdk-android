package com.mobilabsolutions.payment.sample.util

import kotlinx.coroutines.CoroutineDispatcher

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
data class AppCoroutineDispatchers(
    val io: CoroutineDispatcher,
    val computation: CoroutineDispatcher,
    val main: CoroutineDispatcher
)