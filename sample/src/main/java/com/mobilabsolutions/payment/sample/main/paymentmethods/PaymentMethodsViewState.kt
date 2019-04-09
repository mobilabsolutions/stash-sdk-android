package com.mobilabsolutions.payment.sample.main.paymentmethods

import com.airbnb.mvrx.MvRxState

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
data class PaymentMethodsViewState(
        val loading: Boolean = false
) : MvRxState