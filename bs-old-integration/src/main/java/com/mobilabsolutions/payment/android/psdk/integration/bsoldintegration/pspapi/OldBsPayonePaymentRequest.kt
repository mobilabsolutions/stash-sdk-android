package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.pspapi

import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.PaymentMethodRegistrationResponse
import org.simpleframework.xml.*

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Root(name = "soap:Envelope", strict = false)
@NamespaceList (
    Namespace(reference = "http://www.w3.org/2003/05/soap-envelope", prefix = "soap"),
    Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0", prefix = "ns")
)
@Order(elements = ["Header" , "Body"])
data class BsPayonePaymentRequest(
        @field:Element(name = "Header")
        @field:Namespace(reference = "http://www.w3.org/2003/05/soap-envelope")
        val soapHeader: SoapHeader,
        @field:Element(name = "Body")
        @field:Namespace(reference = "http://www.w3.org/2003/05/soap-envelope")
        val soapBody: SoapBody
) {
    companion object {

        fun fromPaymentMethodResponseWithCreditCardData(
                paymentMethodRegistrationResponse: PaymentMethodRegistrationResponse,
                creditCardData: CreditCardData)
                : BsPayonePaymentRequest {
            val bsPayoneCreditCardExpiryDate = BsPayoneCreditCardExpiryDate(
                    creditCardData.expiryDate?.monthValue ?: -1,
                    creditCardData.expiryDate?.year ?: -1
            )
            val bsPayoneCreditCard = BsPayoneCreditCard(
                    creditCardData.number!!,
                    PanAlias(paymentMethodRegistrationResponse.panAlias!!),
                    creditCardData.holder!!,
                    creditCardData.cvv!!,
                    bsPayoneCreditCardExpiryDate
            )
            val paymentRequest = PaymentRequest(
                    paymentMethodRegistrationResponse.merchantId!!,
                    paymentMethodRegistrationResponse.eventExtId!!,
                    "creditcard",
                    paymentMethodRegistrationResponse.action!!,
                    paymentMethodRegistrationResponse.amount!!,
                    paymentMethodRegistrationResponse.currency!!,
                    bsPayoneCreditCard
            )
            val xmlApiRequest = XmlApiRequest(paymentRequest)
            val soapBody = SoapBody(xmlApiRequest)
            return BsPayonePaymentRequest(SoapHeader(), soapBody)


        }
    }

}

@Root(name = "Header")
class SoapHeader()

@Root(name = "Body")
data class SoapBody(
        @field:Element
        val xmlApiRequest: XmlApiRequest
)

@Root(name = "xmlApiRequest")
@Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
data class XmlApiRequest(
        @field:Element
        val paymentRequest: PaymentRequest,
        @field:Attribute
        val id: String? = "a1",
        @field:Attribute
        val version: String? = "1.6"
)

@Root(name = "paymentRequest")
@Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
@Order(elements = ["merchantId", "eventExtId", "kind", "action", "amount", "currency", "creditCard"])
data class PaymentRequest(
        @field:Element
        @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
        val merchantId: String,
        @field:Element
        @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
        val eventExtId: String,
        @field:Element
        @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
        val kind: String = "creditcard",
        @field:Element
        @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
        val action: String,
        @field:Element
        @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
        val amount: String,
        @field:Element
        @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
        val currency: String,
        @field:Element
        @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
        val creditCard: BsPayoneCreditCard,
        @field:Attribute
        val id: String? = "b1"
)

