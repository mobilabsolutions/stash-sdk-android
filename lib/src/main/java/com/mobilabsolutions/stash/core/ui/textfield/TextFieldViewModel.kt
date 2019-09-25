package com.mobilabsolutions.stash.core.ui.textfield

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryController
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryViewModel.CreditCardTextField.CARD_NUMBER
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryViewModel.CreditCardTextField.COUNTRY
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryViewModel.CreditCardTextField.CVV
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryViewModel.CreditCardTextField.EXP_DATE
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryViewModel.CreditCardTextField.NAME
import com.mobilabsolutions.stash.core.util.BaseViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 24-09-2019.
 */
class TextFieldViewModel @AssistedInject constructor(
    @Assisted initialState: TextFieldViewState,
    private val creditCardEntryController: CreditCardEntryController
) : BaseViewModel<TextFieldViewState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: TextFieldViewState): TextFieldViewModel
    }

    companion object : MvRxViewModelFactory<TextFieldViewModel, TextFieldViewState> {
        override fun create(viewModelContext: ViewModelContext, state: TextFieldViewState): TextFieldViewModel? {
            val fragment: TextFieldFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.viewModelFactory.create(state)
        }
    }

    fun onTextChanged(text: String) {
        withState {
            when (it.field) {
                CARD_NUMBER -> creditCardEntryController.onCreditCardNumberChanged(text)
                NAME -> creditCardEntryController.onNameTextChanged(text)
                EXP_DATE -> creditCardEntryController.onExpDateTextChanged(text)
                CVV -> creditCardEntryController.onCvvTextChanged(text)
                COUNTRY -> {
                }
            }
        }
    }

    fun onCreditCardIconChanged(resourceId: Int) {
        creditCardEntryController.onIconResIdChanged(resourceId)
    }
}