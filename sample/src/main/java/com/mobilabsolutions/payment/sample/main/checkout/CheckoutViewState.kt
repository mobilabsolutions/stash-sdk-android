package com.mobilabsolutions.payment.sample.main.checkout

import com.airbnb.mvrx.MvRxState

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
data class CheckoutViewState(
        val loading: Boolean = false
) : MvRxState