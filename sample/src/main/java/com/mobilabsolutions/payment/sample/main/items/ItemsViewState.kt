package com.mobilabsolutions.payment.sample.main.items

import com.airbnb.mvrx.MvRxState

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
data class ItemsViewState(
        val loading: Boolean = false
) : MvRxState