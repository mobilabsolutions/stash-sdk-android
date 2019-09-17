package com.mobilabsolutions.stash.core.ui.creditcard

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
class CreditCardEntryViewModel @AssistedInject constructor(
    @Assisted initialState: CreditCardEntryViewState,
    private val uiRequestHandler: UiRequestHandler
) : BaseViewModel<CreditCardEntryViewState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: CreditCardEntryViewState): CreditCardEntryViewModel
    }

    companion object : MvRxViewModelFactory<CreditCardEntryViewModel, CreditCardEntryViewState> {
        override fun create(viewModelContext: ViewModelContext, state: CreditCardEntryViewState): CreditCardEntryViewModel? {
            val fragment: CreditCardEntryFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.viewModelFactory.create(state)
        }
    }
}