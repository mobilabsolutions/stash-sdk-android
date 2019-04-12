package com.mobilabsolutions.payment.sample.main.items

import com.airbnb.mvrx.MvRxState
import com.mobilabsolutions.payment.sample.data.entities.Product

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
data class ItemsViewState(
        val loading: Boolean = false,
        val products: List<Product> = emptyList()
) : MvRxState