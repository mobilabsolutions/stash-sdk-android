/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.payments.selectpayment

import com.airbnb.mvrx.MvRxState
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod

data class SelectPaymentViewState(
    val amount: Int,
    val paymentMethods: List<PaymentMethod> = emptyList()
) : MvRxState {
    constructor(args: SelectPaymentFragment.Arguments) : this(args.payAmount)
}