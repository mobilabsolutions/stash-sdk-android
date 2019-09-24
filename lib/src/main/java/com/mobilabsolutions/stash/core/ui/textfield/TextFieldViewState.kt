package com.mobilabsolutions.stash.core.ui.textfield

import com.airbnb.mvrx.MvRxState
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryViewModel

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 24-09-2019.
 */
data class TextFieldViewState(
    val position: Int,
    val field: CreditCardEntryViewModel.CreditCardTextField
) : MvRxState {
    constructor(args: TextFieldFragment.Arguments) : this(args.position, CreditCardEntryViewModel.CreditCardTextField.values()[args.position])
}