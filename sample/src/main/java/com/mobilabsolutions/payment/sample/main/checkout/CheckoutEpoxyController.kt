package com.mobilabsolutions.payment.sample.main.checkout

import com.airbnb.epoxy.TypedEpoxyController
import com.mobilabsolutions.payment.sample.checkoutItem
import com.mobilabsolutions.payment.sample.data.resultentities.CartWithProduct

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
class CheckoutEpoxyController(
        private val callbacks: Callbacks
) : TypedEpoxyController<CheckoutViewState>() {
    interface Callbacks {
        fun onAddButtonClicked(cartWithProduct: CartWithProduct)
        fun onRemoveButtonClicked(cartWithProduct: CartWithProduct)
    }

    override fun buildModels(state: CheckoutViewState) {
        if (state.cartItems.isNotEmpty()) {
            state.cartItems.forEach {
                checkoutItem {
                    val product = it.product
                    val cart = it.entry!!
                    id(product.id)
                    product(product)
                    quantity(cart.quantity.toString())
                    addClickListener { _ -> callbacks.onAddButtonClicked(it) }
                    removeClickListener { _ -> callbacks.onRemoveButtonClicked(it) }
                }
            }
        }
    }
}