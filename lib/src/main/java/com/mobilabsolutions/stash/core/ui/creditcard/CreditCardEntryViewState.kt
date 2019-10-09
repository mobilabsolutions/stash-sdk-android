package com.mobilabsolutions.stash.core.ui.creditcard

import com.airbnb.mvrx.MvRxState
import com.mobilabsolutions.stash.core.internal.uicomponents.ValidationResult

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
data class CreditCardEntryViewState(
    val loading: Boolean = false,
    val fields: List<CreditCardEntryViewModel.CreditCardTextField> = CreditCardEntryViewModel.CreditCardTextField.values().toList(),
    val currentPosition: Int = 0,
    val cardNumber: String = "",
    val cardIconResId: Int = -1,
    val name: String = "",
    val expDate: String = "",
    val cvv: String = "",
    val validationResult: ValidationResult? = null
) : MvRxState