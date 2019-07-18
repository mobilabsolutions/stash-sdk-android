/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk.internal

import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface IntegrationInitialization {
    val enabledPaymentMethodTypes: Set<PaymentMethodType>

    fun initialize(stashComponent: StashComponent, url: String = ""): Integration

    fun initializedOrNull(): Integration?
}