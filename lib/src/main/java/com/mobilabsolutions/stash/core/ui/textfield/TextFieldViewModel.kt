package com.mobilabsolutions.stash.core.ui.textfield

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.core.internal.uicomponents.UiRequestHandler
import com.mobilabsolutions.stash.core.util.BaseViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 24-09-2019.
 */
class TextFieldViewModel @AssistedInject constructor(
    @Assisted initialState: TextFieldViewState,
    private val uiRequestHandler: UiRequestHandler
) : BaseViewModel<TextFieldViewState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: TextFieldViewState): TextFieldViewModel
    }

    companion object : MvRxViewModelFactory<TextFieldViewModel, TextFieldViewState> {
        override fun create(viewModelContext: ViewModelContext, state: TextFieldViewState): TextFieldViewModel? {
            val fragment: TextFieldFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.viewModelFactory.create(state)
        }
    }

    init {
        withState {

        }
    }
}