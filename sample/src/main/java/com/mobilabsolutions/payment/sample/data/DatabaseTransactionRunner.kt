/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.sample.data

import androidx.room.withTransaction
import kotlinx.coroutines.CoroutineScope

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
interface DatabaseTransactionRunner {
    suspend operator fun <T> invoke(block: suspend CoroutineScope.() -> T): T
}

class RoomTransactionRunner(private val db: SampleDatabase) : DatabaseTransactionRunner {
    override suspend operator fun <T> invoke(block: suspend CoroutineScope.() -> T): T {
        return db.withTransaction {
            block()
        }
    }
}