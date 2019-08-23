/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import kotlinx.coroutines.flow.Flow

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
@Dao
abstract class PaymentMethodDao : EntityDao<PaymentMethod> {
    @Query("SELECT * FROM payment_method")
    abstract fun entriesObservable(): Flow<List<PaymentMethod>>

    @Query("SELECT COUNT(*) FROM payment_method")
    abstract suspend fun paymentMethodsCount(): Int

    @Query("SELECT * FROM payment_method WHERE paymentMethod_id=:paymentMethodId")
    abstract suspend fun entityBypaymentMethodId(paymentMethodId: String): PaymentMethod?
}