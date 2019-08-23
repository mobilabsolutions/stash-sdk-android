/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.cart

import com.mobilabsolutions.stash.sample.data.resultentities.CartWithProduct
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
interface CartRepository {
    fun observeCarts(): Observable<List<CartWithProduct>>
    fun observeCartsFlow(): Flow<List<CartWithProduct>>
    suspend fun changeCartQuantity(add: Boolean, cartWithProduct: CartWithProduct)
    suspend fun addProductToCart(productId: Long)
}