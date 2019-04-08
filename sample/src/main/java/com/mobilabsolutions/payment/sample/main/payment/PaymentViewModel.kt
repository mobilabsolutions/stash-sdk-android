package com.mobilabsolutions.payment.sample.main.payment

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
class PaymentViewModel @AssistedInject constructor(
        @Assisted initialState: PaymentViewState,
        private val schedulers: AppRxSchedulers
) : BaseViewModel<PaymentViewState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: PaymentViewState): PaymentViewModel
    }

    companion object : MvRxViewModelFactory<PaymentViewModel, PaymentViewState> {
        override fun create(viewModelContext: ViewModelContext, state: PaymentViewState): PaymentViewModel? {
            val fragment: PaymentFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.paymentViewModelFactory.create(state)
        }
    }
}