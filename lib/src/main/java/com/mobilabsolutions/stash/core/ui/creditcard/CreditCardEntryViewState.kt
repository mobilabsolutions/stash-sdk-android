package com.mobilabsolutions.stash.core.ui.creditcard

import com.airbnb.mvrx.MvRxState

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
data class CreditCardEntryViewState(
    val loading: Boolean = false
) : MvRxState