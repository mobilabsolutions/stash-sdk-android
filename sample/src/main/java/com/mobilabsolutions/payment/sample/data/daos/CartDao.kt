package com.mobilabsolutions.payment.sample.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.mobilabsolutions.payment.sample.data.entities.Cart
import com.mobilabsolutions.payment.sample.data.resultentities.CartWithProduct
import io.reactivex.Observable

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Dao
abstract class CartDao : EntityDao<Cart> {

    @Transaction
    @Query("SELECT * FROM cart")
    abstract fun entriesObservable(): Observable<List<CartWithProduct>>
}