package com.mobilabsolutions.payment.android.psdk.internal.api.hypercharge

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Root(name = "payment")
class HyperchargeSepaPaymentRequest {

    @field:Element(name = "payment_method", required = false)
    var paymentMethod: String? = null

    @field:Element(name = "bank_account_holder", required = false)
    var bankAccountHolder: String? = null

    @field:Element(name = "bank_account_number", required = false)
    var bankAccountNumber: String? = null

    @field:Element(name = "bank_number", required = false)
    var bankNumber: String? = null
}