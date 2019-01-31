package com.mobilabsolutions.payment.android.psdk

import com.mobilabsolutions.payment.android.psdk.internal.psphandler.psppaypal.PayPalActivityCustomization

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface UiCustomizationManager {
    fun setPaypalRedirectActivityCustomizations(payPalActivityCustomization: PayPalActivityCustomization)
}