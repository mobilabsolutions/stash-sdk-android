/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.main.paymentmethods

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.core.ExtraAliasInfo
import com.mobilabsolutions.stash.core.PaymentMethodAlias
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.Stash
import com.mobilabsolutions.stash.sample.core.BaseViewModel
import com.mobilabsolutions.stash.sample.core.launchInteractor
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.data.entities.User
import com.mobilabsolutions.stash.sample.data.interactors.AddPaymentMethod
import com.mobilabsolutions.stash.sample.data.interactors.DeletePaymentMethod
import com.mobilabsolutions.stash.sample.data.interactors.UpdatePaymentMethods
import com.mobilabsolutions.stash.sample.data.interactors.UpdateUser
import com.mobilabsolutions.stash.sample.util.AppRxSchedulers
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
class PaymentMethodsViewModel @AssistedInject constructor(
    @Assisted initialStateMethods: PaymentMethodsViewState,
    updateUser: UpdateUser,
    updatePaymentMethods: UpdatePaymentMethods,
    private val deletePaymentMethod: DeletePaymentMethod,
    private val addPaymentMethod: AddPaymentMethod,
    private val schedulers: AppRxSchedulers
) : BaseViewModel<PaymentMethodsViewState>(initialStateMethods) {

    private val _error = MutableLiveData<Throwable>()

    val error: LiveData<Throwable>
        get() = _error

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

    init {
        updateUser.observe()
            .subscribeOn(schedulers.io)
            .doOnNext {
                disposables += updatePaymentMethods.errorSubject.observeOn(schedulers.main).subscribe(this::onError)

                // Prevent the first time payment method list update, with an invalid user id.
                if (it.userId != User.EMPTY_USER.userId) {
                    scope.launchInteractor(updatePaymentMethods, UpdatePaymentMethods.ExecuteParams(userId = it.userId))
                }
            }
            .execute {
                copy(user = it() ?: User.EMPTY_USER)
            }

        updatePaymentMethods.observe()
            .subscribeOn(schedulers.io)
            .execute {
                copy(paymentMethods = it() ?: emptyList())
            }
        updateUser.setParams(Unit)
        updatePaymentMethods.setParams(Unit)
    }

    fun onAddBtnClicked() {
        disposables += Stash.getRegistrationManager().registerPaymentMethodUsingUi(/* idempotencyKey = UUID.randomUUID() */)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.main)
            .subscribe(this::onRegisterPaymentSuccess, this::onError)
    }

    fun onDeleteBtnClicked(paymentMethod: PaymentMethod) {
        disposables += deletePaymentMethod.errorSubject.observeOn(schedulers.main).subscribe(this::onError)
        scope.launchInteractor(deletePaymentMethod, DeletePaymentMethod.ExecuteParams(paymentMethod))
    }

    private fun onRegisterPaymentSuccess(paymentMethodAlias: PaymentMethodAlias) {
        val type = when (paymentMethodAlias.paymentMethodType) {
            PaymentMethodType.CC -> "CC"
            PaymentMethodType.SEPA -> "SEPA"
            PaymentMethodType.PAYPAL -> "PAY_PAL"
        }
        withState {
            val paymentMethod = when (val aliasInfo = paymentMethodAlias.extraAliasInfo) {
                is ExtraAliasInfo.CreditCardExtraInfo -> PaymentMethod(_type = type,
                    mask = aliasInfo.creditCardMask,
                    _cardType = aliasInfo.creditCardType.name,
                    expiryMonth = aliasInfo.expiryMonth.toString(),
                    expiryYear = aliasInfo.expiryYear.toString()
                )
                is ExtraAliasInfo.SepaExtraInfo -> PaymentMethod(_type = type,
                    iban = aliasInfo.maskedIban
                )
                is ExtraAliasInfo.PaypalExtraInfo -> PaymentMethod(_type = type,
                    email = aliasInfo.email
                )
            }
            disposables += addPaymentMethod.errorSubject.observeOn(schedulers.main).subscribe(this::onError)
            scope.launchInteractor(addPaymentMethod, AddPaymentMethod.ExecuteParams(userId = it.user.userId, aliasId = paymentMethodAlias.alias, paymentMethod = paymentMethod))
        }
    }

    private fun onError(throwable: Throwable) {
        _error.value = throwable
        Timber.e(throwable)
    }

    fun clearError() {
        _error.value = null
    }
}