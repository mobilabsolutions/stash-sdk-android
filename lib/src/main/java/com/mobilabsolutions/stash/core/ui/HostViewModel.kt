package com.mobilabsolutions.stash.core.ui

import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.core.util.AppRxSchedulers
import com.mobilabsolutions.stash.core.util.BaseViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
class HostViewModel @AssistedInject constructor(
    @Assisted initialState: HostViewState,
    private val schedulers: AppRxSchedulers
) : BaseViewModel<HostViewState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: HostViewState): HostViewModel
    }

    companion object : MvRxViewModelFactory<HostViewModel, HostViewState> {
        override fun create(viewModelContext: ViewModelContext, state: HostViewState): HostViewModel? {
            val fragment: HostActivity = viewModelContext.activity()
            return fragment.hostViewModelFactory.create(state)
        }
    }
}