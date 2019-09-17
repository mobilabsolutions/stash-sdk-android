package com.mobilabsolutions.stash.core.ui.sepa

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.core.internal.uicomponents.UiRequestHandler
import com.mobilabsolutions.stash.core.util.BaseViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
class SepaEntryViewModel @AssistedInject constructor(
    @Assisted initialState: SepaEntryViewState,
    private val uiRequestHandler: UiRequestHandler
) : BaseViewModel<SepaEntryViewState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: SepaEntryViewState): SepaEntryViewModel
    }

    companion object : MvRxViewModelFactory<SepaEntryViewModel, SepaEntryViewState> {
        override fun create(viewModelContext: ViewModelContext, state: SepaEntryViewState): SepaEntryViewModel? {
            val fragment: SepaEntryFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.viewModelFactory.create(state)
        }
    }
}