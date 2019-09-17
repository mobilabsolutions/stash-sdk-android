package com.mobilabsolutions.stash.core.ui.picker

import com.airbnb.mvrx.MvRxState
import com.mobilabsolutions.stash.core.PaymentMethodType

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
data class PaymentPickerViewState(
    val loading: Boolean = false,
    val availablePaymentMethods: List<PaymentMethodType> = emptyList()
) : MvRxState