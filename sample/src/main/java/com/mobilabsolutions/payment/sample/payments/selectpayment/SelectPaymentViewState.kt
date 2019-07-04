/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.sample.payments.selectpayment

import com.airbnb.mvrx.MvRxState
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod

data class SelectPaymentViewState(
    val loading: Boolean = false,
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val amount: Int = 0
) : MvRxState