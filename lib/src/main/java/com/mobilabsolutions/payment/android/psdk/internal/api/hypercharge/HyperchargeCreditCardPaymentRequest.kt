package com.mobilabsolutions.payment.android.psdk.internal.api.hypercharge

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Root(name = "payment")
class HyperchargeCreditCardPaymentRequest {

    @field:Element(name = "payment_method")
    var paymentMethod: String? = null

    @field:Element(name = "card_holder")
    var cardHolder: String? = null

    @field:Element(name = "expiration_month")
    var expirationMonth: String? = null

    @field:Element(name = "expiration_year")
    var expirationYear: String? = null

    @field:Element(name = "card_number")
    var cardNumber: String? = null

    @field:Element(name = "cvv")
    var cvv: String? = null

}