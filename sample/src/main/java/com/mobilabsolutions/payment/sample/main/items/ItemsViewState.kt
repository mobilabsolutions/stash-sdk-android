package com.mobilabsolutions.payment.sample.main.items

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
data class ItemsViewState(
        val items: Async<List<Item>> = Uninitialized
) : MvRxState