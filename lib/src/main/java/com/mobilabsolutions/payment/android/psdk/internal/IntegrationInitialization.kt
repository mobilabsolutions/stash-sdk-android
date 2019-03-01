package com.mobilabsolutions.payment.android.psdk.internal

import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface IntegrationInitialization {
    fun initialize(paymentSdkComponent: PaymentSdkComponent, url : String = ""): Integration

    fun initializedOrNull(): Integration?
}