package com.mobilabsolutions.payment.sample.main.paymentmethods

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.sample.core.BaseViewModel
import com.mobilabsolutions.payment.sample.core.launchInteractor
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.data.interactors.DeletePaymentMethod
import com.mobilabsolutions.payment.sample.data.interactors.LoadPaymentMethods
import com.mobilabsolutions.payment.sample.util.AppRxSchedulers
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
class PaymentMethodsViewModel @AssistedInject constructor(
    @Assisted initialStateMethods: PaymentMethodsViewState,
    schedulers: AppRxSchedulers,
    loadPaymentMethods: LoadPaymentMethods,
    private val deletePaymentMethod: DeletePaymentMethod
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

    init {
        loadPaymentMethods.observe()
                .subscribeOn(schedulers.io)
                .execute {
                    copy(paymentMethods = it() ?: emptyList())
                }

        loadPaymentMethods.setParams(Unit)
    }

    fun onAddBtnClicked() {
        val registrationManager = PaymentSdk.getRegistrationManager()
        registrationManager.registerPaymentMehodUsingUi().subscribeBy (
            onSuccess = {
                Timber.d("Got method $it")
            },
            onError = {
                Timber.d("Failed $it")
            }
        )
    }

    fun onDeleteBtnClicked(paymentMethod: PaymentMethod) {
        scope.launchInteractor(deletePaymentMethod, DeletePaymentMethod.ExecuteParams(paymentMethod))
    }
}