/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.payments.selectpayment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.sample.core.BaseViewModel
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.domain.interactors.AuthorisePayment
import com.mobilabsolutions.stash.sample.domain.invoke
import com.mobilabsolutions.stash.sample.domain.launchObserve
import com.mobilabsolutions.stash.sample.domain.observers.ObservePaymentMethods
import com.mobilabsolutions.stash.sample.network.request.AuthorizePaymentRequest
import com.mobilabsolutions.stash.sample.util.AppRxSchedulers
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.catch

class SelectPaymentViewModel @AssistedInject constructor(
    @Assisted initialStateMethods: SelectPaymentViewState,
    val schedulers: AppRxSchedulers,
    observePaymentMethods: ObservePaymentMethods,
    private val authorisePayment: AuthorisePayment
) : BaseViewModel<SelectPaymentViewState>(initialStateMethods) {

    private var lastSelectedPaymentMethod: PaymentMethod? = null

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
        viewModelScope.launchObserve(observePaymentMethods) {
            it.execute { result -> copy(paymentMethods = result().orEmpty()) }
        }
        observePaymentMethods()
    }

    fun onPaymentMethodSelected(paymentMethod: PaymentMethod) {
        lastSelectedPaymentMethod = paymentMethod
    }

    fun onPayClicked() {
        lastSelectedPaymentMethod?.run {
            withState {
                val authorizePaymentRequest = AuthorizePaymentRequest(
                    amount = it.amount,
                    currency = "EUR",
                    paymentMethodId = this.paymentMethodId,
                    reason = "Nothing"
                )
                authorisePayment(AuthorisePayment.Params(authorizePaymentRequest))
                    .also {
                        it.catch { errorChannel.offer(it) }
                    }
            }
        }
    }
}