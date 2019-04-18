package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.pspapi

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Order
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text

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
    val date: BsPayoneCreditCardExpiryDate
)

@Root
@Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
data class BsPayoneCreditCardExpiryDate(
    @field:Element
    @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
    val month: Int,
    @field:Element
    @field:Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
    val year: Int
)

@Root
@Namespace(reference = "http://www.voeb-zvd.de/xmlapi/1.0")
data class PanAlias(
    @field:Text val panalias: String,
    @field:Attribute val generate: Boolean = true
)
