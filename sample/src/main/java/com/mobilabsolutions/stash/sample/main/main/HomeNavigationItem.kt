package com.mobilabsolutions.stash.sample.main.main

import com.mobilabsolutions.stash.sample.R

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 16-08-2019.
 */
enum class HomeNavigationItem(val destinationId: Int) {
    ITEMS(R.id.navigation_items),
    CHECKOUT(R.id.navigation_checkout),
    PAYMENT(R.id.navigation_payment)
}

fun homeNavigationItemForDestinationId(destinationId: Int): HomeNavigationItem? {
    return HomeNavigationItem.values().find { it.destinationId == destinationId }
}
