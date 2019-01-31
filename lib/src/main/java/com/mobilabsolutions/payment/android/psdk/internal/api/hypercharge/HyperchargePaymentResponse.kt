package com.mobilabsolutions.payment.android.psdk.internal.api.hypercharge

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Root(name = "payment_response")
class HyperchargePaymentResponse {

    @field:Element(name = "type", required = false)
    var transactionType: String? = null

    @field:Element(name = "status")
    var status: String? = null

    @field:Element(name = "unique_id", required = false)
    var uniqueId: String? = null

    @field:Element(name = "transaction_id", required = false)
    var transactionId: String? = null

    @field:Element(name = "mode", required = false)
    var mode: String? = null

    @field:Element(name = "timestamp", required = false)
    var timestamp: String? = null

    @field:Element(name = "amount", required = false)
    var amount: String? = null

    @field:Element(name = "currency", required = false)
    var currency: String? = null

    /** Only in case status=error  */
    @field:Element(name = "code", required = false)
    var code: Int = 0

    /** Only in case status=error  */
    @field:Element(name = "technical_message", required = false)
    var technicalMessage: String? = null

    /** Only in case status=error  */
    @field:Element(name = "message", required = false)
    var message: String? = null

}