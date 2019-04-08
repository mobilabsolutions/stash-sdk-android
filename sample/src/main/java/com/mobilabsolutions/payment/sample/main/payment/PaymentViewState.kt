package com.mobilabsolutions.payment.sample.main.payment

import com.airbnb.mvrx.MvRxState

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
data class PaymentViewState(
        val loading: Boolean = false
) : MvRxState