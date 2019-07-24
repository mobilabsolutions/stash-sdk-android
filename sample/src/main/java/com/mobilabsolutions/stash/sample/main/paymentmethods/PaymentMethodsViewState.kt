/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.main.paymentmethods

import com.airbnb.mvrx.MvRxState
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.data.entities.User

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
data class PaymentMethodsViewState(
    val loading: Boolean = false,
    val user: User = User.EMPTY_USER,
    val paymentMethods: List<PaymentMethod> = emptyList()
) : MvRxState