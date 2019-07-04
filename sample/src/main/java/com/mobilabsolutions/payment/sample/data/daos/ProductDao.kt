/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.sample.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.mobilabsolutions.payment.sample.data.entities.Product
import io.reactivex.Observable

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Dao
abstract class ProductDao : EntityDao<Product> {
    @Query("SELECT COUNT(*) FROM product")
    abstract suspend fun productCount(): Int

    @Query("SELECT * FROM product")
    abstract fun entriesObservable(): Observable<List<Product>>

    @Query("SELECT * FROM product")
    abstract suspend fun products(): List<Product>
}