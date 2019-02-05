package com.mobilabsolutions.payment.android.psdk.internal.api.backend

import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.SepaData

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PaymentMethodRegistrationRequest {
    var accountData: SepaData? = null
        //TODO temporary until we move billing data
        set(value) {
            field = value
            billingData = if (value != null) {
                val billingData = BillingData.fromName(value.holder)
                billingData.email = "test@test.test"
                billingData.address1 = "Test"
                billingData.city = "Test"
                billingData.zip = "12345"
                billingData
            } else {
                null
            }
        }

    var cardMask: String? = null
    var customerId: String? = null
    var oneTimePayment: Boolean = false
    //TODO temporary
    private var billingData: BillingData? = null
}