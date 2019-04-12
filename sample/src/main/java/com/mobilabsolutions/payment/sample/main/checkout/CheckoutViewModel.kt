package com.mobilabsolutions.payment.sample.main.checkout

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.payment.sample.core.BaseViewModel
import com.mobilabsolutions.payment.sample.util.AppRxSchedulers
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class CheckoutViewModel @AssistedInject constructor(
        @Assisted initialState: CheckoutViewState,
        private val schedulers: AppRxSchedulers
) : BaseViewModel<CheckoutViewState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: CheckoutViewState): CheckoutViewModel
    }

    companion object : MvRxViewModelFactory<CheckoutViewModel, CheckoutViewState> {
        override fun create(viewModelContext: ViewModelContext, state: CheckoutViewState): CheckoutViewModel? {
            val fragment: CheckoutFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.checkoutViewModelFactory.create(state)
        }
    }

    fun onPayBtnClicked(){

    }
}