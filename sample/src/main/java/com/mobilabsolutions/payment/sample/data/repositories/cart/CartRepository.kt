package com.mobilabsolutions.payment.sample.data.repositories.cart

import com.mobilabsolutions.payment.sample.data.resultentities.CartWithProduct
import com.mobilabsolutions.payment.sample.util.AppCoroutineDispatchers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Singleton
class CartRepository @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val localCartStore: LocalCartStore
) {

    init {
        // remove this after merging.
//        GlobalScope.launch(dispatchers.io) {
//            localCartStore.insertSampleData()
//        }
    }

    fun observeCarts() = localCartStore.observeCarts()

    suspend fun changeCartQuantity(add: Boolean, cartWithProduct: CartWithProduct) {
        if (add) {
            addCart(cartWithProduct)
        } else {
            removeCart(cartWithProduct)
        }
    }

    suspend fun addCart(cartWithProduct: CartWithProduct) {
        localCartStore.addCart(cartWithProduct)
    }

    suspend fun removeCart(cartWithProduct: CartWithProduct) {
        localCartStore.removeCart(cartWithProduct)
    }
}