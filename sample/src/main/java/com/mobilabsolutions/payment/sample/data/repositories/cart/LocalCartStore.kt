package com.mobilabsolutions.payment.sample.data.repositories.cart

import com.mobilabsolutions.payment.sample.data.DatabaseTransactionRunner
import com.mobilabsolutions.payment.sample.data.daos.CartDao
import com.mobilabsolutions.payment.sample.data.entities.Cart
import com.mobilabsolutions.payment.sample.data.resultentities.CartWithProduct
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 15-04-2019.
 */
class LocalCartStore @Inject constructor(
    private val transactionRunner: DatabaseTransactionRunner,
    private val cartDao: CartDao
) {

    fun observeCarts() = cartDao.entriesObservable()

    suspend fun addCartQuantity(cartWithProduct: CartWithProduct) = transactionRunner {
        val cart = cartWithProduct.entry!!
        val updateCart = cart.copy(quantity = cart.quantity + 1)
        cartDao.update(updateCart)
    }

    suspend fun removeCart(cartWithProduct: CartWithProduct) = transactionRunner {
        val cart = cartWithProduct.entry!!
        if (cart.quantity == 1) {
            cartDao.delete(cart)
        } else {
            val updateCart = cart.copy(quantity = cart.quantity - 1)
            cartDao.update(updateCart)
        }
    }

    suspend fun addProductToCart(productId: Long) = transactionRunner {
        val cart = cartDao.cartByProductId(productId)
        if (cart == null) {
            cartDao.insert(Cart(productId = productId, quantity = 1))
        } else {
            cartDao.update(cart.copy(quantity = cart.quantity + 1))
        }
    }

    suspend fun emptyCart() = transactionRunner {
        cartDao.clearCart()
    }
}