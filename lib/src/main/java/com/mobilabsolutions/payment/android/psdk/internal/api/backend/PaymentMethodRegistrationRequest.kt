package com.mobilabsolutions.payment.android.psdk.internal.api.backend

import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.SepaData

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PaymentMethodRegistrationRequest {
    var accountData : SepaData? = null
    var cardMask : String? = null
    var customerId : String? = null
    var oneTimePayment : Boolean = false
}