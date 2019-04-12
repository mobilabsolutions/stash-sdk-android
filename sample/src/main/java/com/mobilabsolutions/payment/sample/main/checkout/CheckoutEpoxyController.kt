package com.mobilabsolutions.payment.sample.main.checkout

import com.airbnb.epoxy.TypedEpoxyController
import com.mobilabsolutions.payment.sample.checkoutItem
import com.mobilabsolutions.payment.sample.data.entities.Product

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
class CheckoutEpoxyController(
        private val callbacks: Callbacks
) : TypedEpoxyController<CheckoutViewState>() {
    interface Callbacks {
        fun onAddButtonClicked(product: Product)
        fun onMinusButtonClicked(product: Product)
    }

    override fun buildModels(state: CheckoutViewState) {
        checkoutItem {
            id("test")
        }


        if (state.cartItems.isNotEmpty()) {
            state.cartItems.forEach {
                checkoutItem {
                    id(it.product.id)

                }
            }
        }
    }
}