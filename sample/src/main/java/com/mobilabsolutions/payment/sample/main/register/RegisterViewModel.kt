package com.mobilabsolutions.payment.sample.main.register

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.payment.sample.core.BaseViewModel
import com.mobilabsolutions.payment.sample.util.AppRxSchedulers
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
class RegisterViewModel @AssistedInject constructor(
        @Assisted initialState: RegisterViewState,
        private val schedulers: AppRxSchedulers
) : BaseViewModel<RegisterViewState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: RegisterViewState): RegisterViewModel
    }

    companion object : MvRxViewModelFactory<RegisterViewModel, RegisterViewState> {
        override fun create(viewModelContext: ViewModelContext, state: RegisterViewState): RegisterViewModel? {
            val fragment: RegisterFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.registerViewModelFactory.create(state)
        }
    }
}