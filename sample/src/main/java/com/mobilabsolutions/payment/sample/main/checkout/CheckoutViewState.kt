package com.mobilabsolutions.payment.sample.main.checkout

import com.airbnb.mvrx.MvRxState
import com.mobilabsolutions.payment.sample.data.resultentities.CartWithProduct

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
data class CheckoutViewState(
        val loading: Boolean = false,
        val cartItems: List<CartWithProduct> = emptyList(),
        val totalAmount: Int = 0
) : MvRxState