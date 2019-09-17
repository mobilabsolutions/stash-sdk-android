package com.mobilabsolutions.stash.core.ui.picker

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.core.internal.uicomponents.UiRequestHandler
import com.mobilabsolutions.stash.core.util.BaseViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
class PaymentPickerViewModel @AssistedInject constructor(
    @Assisted initialState: PaymentPickerViewState,
    private val uiRequestHandler: UiRequestHandler
) : BaseViewModel<PaymentPickerViewState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: PaymentPickerViewState): PaymentPickerViewModel
    }

    companion object : MvRxViewModelFactory<PaymentPickerViewModel, PaymentPickerViewState> {
        override fun create(viewModelContext: ViewModelContext, state: PaymentPickerViewState): PaymentPickerViewModel? {
            val fragment: PaymentPickerFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.viewModelFactory.create(state)
        }
    }

    init {
        setState {
            copy(availablePaymentMethods = uiRequestHandler.availablePaymentMethods())
        }
    }
}
