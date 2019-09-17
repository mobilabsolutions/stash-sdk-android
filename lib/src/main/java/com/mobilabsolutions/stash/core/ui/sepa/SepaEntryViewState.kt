package com.mobilabsolutions.stash.core.ui.sepa

import com.airbnb.mvrx.MvRxState

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
data class SepaEntryViewState(
    val loading: Boolean = false
) : MvRxState