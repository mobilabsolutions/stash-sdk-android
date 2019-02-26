package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.pspapi

import org.simpleframework.xml.Element
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */

@Root(name = "Envelope", strict = false)
data class BsPayonePaymentResponse(
        @field:Path("Body")
        @field:Element(name = "xmlApiResponse")
        var apiResponse: XmlApiResponse? = null

)

@Root(name = "xmlApiResponse", strict = false)
data class XmlApiResponse(
        @field:Element(name = "paymentResponse", required = false)
        var response: PaymentResponse? = null,

        @field:Element(name = "panAliasResponse", required = false)
        var aliasResponse: AliasResponse? = null
)

@Root(name = "panAliasResponse", strict = false)
data class AliasResponse(

        @field:Element(name = "rc", required = false)
        var rc: Int = 0,

        @field:Element(name = "panalias", required = false)
        var alias: String? = null
)


@Root(name = "creditCard", strict = false)
data class EnvelopeCreditCard(

        @field:Element(required = false)
        var pan: String? = null,

        @field:Element(name = "panalias", required = false)
        var panAlias: String? = null,

        @field:Element(required = false)
        var holder: String? = null,

        @field:Element(required = false)
        var verificationCode: String? = null
)

data class EnvelopeCardExpDate(

        @field:Element(required = false)
        var month: String? = null,

        @field:Element(required = false)
        var year: String? = null
)

@Root(name = "paymentResponse", strict = false)
data class PaymentResponse(

        @field:Element(name = "timeStamp", required = false)
        var timestamp: String? = null,

        @field:Element(required = false)
        var eventExtId: String? = null,

        @field:Element(required = false)
        var kind: String? = null,

        @field:Element(required = false)
        var action: String? = null,

        @field:Element(required = false)
        var amount: String? = null,

        @field:Element(required = false)
        var currency: String? = null,

        @field:Element
        var rc: Int = 0,

        @field:Element
        var message: String? = null,

        @field:Element(required = false)
        var backRc: String? = null,

        @field:Element(required = false)
        var txtExtId: String? = null,

        @field:Element(required = false)
        var aid: Int = 0,

        @field:Element(required = false)
        var creditCard: EnvelopeCreditCard? = null
)