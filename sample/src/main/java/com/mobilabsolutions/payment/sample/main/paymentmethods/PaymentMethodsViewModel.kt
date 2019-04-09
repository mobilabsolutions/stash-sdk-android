package com.mobilabsolutions.payment.sample.main.paymentmethods

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
class PaymentMethodsViewModel @AssistedInject constructor(
        @Assisted initialStateMethods: PaymentMethodsViewState,
        private val schedulers: AppRxSchedulers
) : BaseViewModel<PaymentMethodsViewState>(initialStateMethods) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialStateMethods: PaymentMethodsViewState): PaymentMethodsViewModel
    }

    companion object : MvRxViewModelFactory<PaymentMethodsViewModel, PaymentMethodsViewState> {
        override fun create(viewModelContext: ViewModelContext, state: PaymentMethodsViewState): PaymentMethodsViewModel? {
            val methodsFragment: PaymentMethodsFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return methodsFragment.paymentMethodsViewModelFactory.create(state)
        }
    }
}