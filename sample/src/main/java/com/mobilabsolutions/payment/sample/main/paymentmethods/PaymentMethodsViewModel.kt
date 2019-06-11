package com.mobilabsolutions.payment.sample.main.paymentmethods

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.payment.android.psdk.ExtraAliasInfo
import com.mobilabsolutions.payment.android.psdk.PaymentMethodAlias
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.sample.core.BaseViewModel
import com.mobilabsolutions.payment.sample.core.launchInteractor
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.data.entities.User
import com.mobilabsolutions.payment.sample.data.interactors.AddPaymentMethod
import com.mobilabsolutions.payment.sample.data.interactors.DeletePaymentMethod
import com.mobilabsolutions.payment.sample.data.interactors.UpdatePaymentMethods
import com.mobilabsolutions.payment.sample.data.interactors.UpdateUser
import com.mobilabsolutions.payment.sample.util.AppRxSchedulers
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
                scope.launchInteractor(updatePaymentMethods, UpdatePaymentMethods.ExecuteParams(userId = it.userId))
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
        disposables += PaymentSdk.getRegistrationManager().registerPaymentMethodUsingUi()
            .subscribeOn(schedulers.io)
            .subscribe(this::onRegisterPaymentSuccess, this::onError)
    }

    fun onDeleteBtnClicked(paymentMethod: PaymentMethod) {
        scope.launchInteractor(deletePaymentMethod, DeletePaymentMethod.ExecuteParams(paymentMethod))
    }

    private fun onRegisterPaymentSuccess(paymentMethodAlias: PaymentMethodAlias) {
        val type = when (paymentMethodAlias.paymentMethodType) {
            PaymentMethodType.CC -> "CC"
            PaymentMethodType.SEPA -> "SEPA"
            PaymentMethodType.PAYPAL -> "PAY_PAL"
        }
        withState {
            val alias = when (val aliasInfo = paymentMethodAlias.extraAliasInfo) {
                is ExtraAliasInfo.CreditCardExtraInfo -> aliasInfo.creditCardMask
                is ExtraAliasInfo.SepaExtraInfo -> aliasInfo.maskedIban
                is ExtraAliasInfo.PaypalExtraInfo -> aliasInfo.email
            }
            val paymentMethod = PaymentMethod(alias = alias, _type = type)
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