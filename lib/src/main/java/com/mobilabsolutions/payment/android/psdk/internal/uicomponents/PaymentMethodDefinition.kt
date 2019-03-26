package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import com.mobilabsolutions.payment.android.psdk.PaymentMethodType

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class PaymentMethodDefinition(
        val methodId : String,
        val pspIdentifier : String,
        val paymentMethodType : PaymentMethodType

)


