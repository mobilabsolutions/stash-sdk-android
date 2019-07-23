/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.sample.payments.selectpayment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.payment.sample.core.BaseViewModel
import com.mobilabsolutions.payment.sample.core.launchInteractor
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.data.interactors.AuthorizePayment
import com.mobilabsolutions.payment.sample.data.interactors.UpdatePaymentMethods
import com.mobilabsolutions.payment.sample.network.request.AuthorizePaymentRequest
import com.mobilabsolutions.payment.sample.util.AppRxSchedulers
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.rxkotlin.plusAssign

class SelectPaymentViewModel @AssistedInject constructor(
    @Assisted initialStateMethods: SelectPaymentViewState,
    val schedulers: AppRxSchedulers,
    updatePaymentMethods: UpdatePaymentMethods,
    private val authorizePayment: AuthorizePayment
) : BaseViewModel<SelectPaymentViewState>(initialStateMethods) {

    private var lastSelectedPaymentMethod: PaymentMethod? = null

    private val _error = MutableLiveData<Throwable>()

    val error: LiveData<Throwable>
        get() = _error

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

        disposables += authorizePayment.errorSubject
                .subscribeOn(schedulers.io)
                .observeOn(schedulers.main)
                .subscribe(_error::setValue)

        updatePaymentMethods.observe()
                .subscribeOn(schedulers.io)
                .execute {
                    copy(paymentMethods = it() ?: emptyList())
                }

        updatePaymentMethods.setParams(Unit)
    }

    fun setAmount(amount: Int) {
        setState { copy(amount = amount) }
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
                viewModelScope.launchInteractor(authorizePayment, AuthorizePayment.ExecuteParams(authorizePaymentRequest = authorizePaymentRequest))
            }
        }
    }
}