package com.mobilabsolutions.payment.android.psdk.internal.api.oldbspayone

import org.simpleframework.xml.*

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Root(name = "creditCard")
@Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
@Order(elements = ["pan", "panalias", "expiryDate", "holder", "verificationCode"])
data class BsPayoneCreditCard(
        @field:Element
        @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
        val pan: String,
        @field:Element(name = "panalias")
        @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
        val panAlias: PanAlias,
        @field:Element
        @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
        val holder: String,
        @field:Element
        @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
        val verificationCode: String,
        @field:Element(name = "expiryDate")
        @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
        val date: BsPayoneCreditCardExpiryDate)

@Root
@Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
data class BsPayoneCreditCardExpiryDate(
        @field:Element
        @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
        val month : Int,
        @field:Element
        @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
        val year : Int
)

@Root
@Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
data class PanAlias(
        @field:Text val panalias : String,
        @field:Attribute val generate : Boolean = true
        )
