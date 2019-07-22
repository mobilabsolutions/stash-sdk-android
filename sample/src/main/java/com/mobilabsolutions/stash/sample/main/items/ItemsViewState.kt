/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.main.items

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.mobilabsolutions.stash.sample.data.entities.Product

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
data class ItemsViewState(
    val loading: Boolean = false,
    val products: Async<List<Product>> = Uninitialized
) : MvRxState