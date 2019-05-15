package com.mobilabsolutions.payment.sample.payments.selectpayment

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.payment.sample.core.BaseViewModel
import com.mobilabsolutions.payment.sample.data.interactors.LoadPaymentMethods
import com.mobilabsolutions.payment.sample.util.AppRxSchedulers
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class SelectPaymentViewModel @AssistedInject constructor(
    @Assisted initialStateMethods: SelectPaymentViewState,
    loadPaymentMethods: LoadPaymentMethods,
    schedulers: AppRxSchedulers
) : BaseViewModel<SelectPaymentViewState>(initialStateMethods) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialStateMethods: SelectPaymentViewState): SelectPaymentViewModel
    }

    companion object : MvRxViewModelFactory<SelectPaymentViewModel, SelectPaymentViewState> {
        override fun create(viewModelContext: ViewModelContext, state: SelectPaymentViewState): SelectPaymentViewModel? {
            val methodsFragment: SelectPaymentFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return methodsFragment.viewModelFactory.create(state)
        }
    }

    init {
        loadPaymentMethods.observe()
            .subscribeOn(schedulers.io)
            .execute {
                copy(paymentMethods = it() ?: emptyList())
            }

        loadPaymentMethods.setParams(Unit)
    }

    fun setAmount(amount: Int) {
        setState { copy(amount = amount) }
    }

    fun onSelection() {
        // TODO: Biju: Hit Merchant Backend
    }
}