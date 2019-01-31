package com.mobilabsolutions.payment.android.psdk.internal.psphandler.psppaypal

import java.io.Serializable

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class PayPalActivityCustomization (
        val showAppBar : Boolean = false,
        val showUpNavigation : Boolean = false
) : Serializable