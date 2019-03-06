package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration

import com.mobilabsolutions.payment.android.psdk.model.CreditCardData

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class BsOldRegistrationRequest(
        var url: String? = null,
        var merchantId: String,
        var action: String? = null,
        var panAlias: String,
        var username: String? = null,
        var password: String? = null,
        var eventExtId: String? = null,
        var amount: String? = null,
        var currency: String? = null,
        var kind: String? = null,
        val creditCardData: CreditCardData
) {
    companion object {
        fun fromMapWitchCCData(extraData: Map<String, String>, creditCardData: CreditCardData): BsOldRegistrationRequest {
            return BsOldRegistrationRequest(
                    url = extraData["url"] ?: throw BsOldIntegrationException("url parameter missing"),
                    merchantId = extraData["merchantId"]
                            ?: throw BsOldIntegrationException("merchantId parameter missing"),
                    action = extraData["action"]
                            ?: throw BsOldIntegrationException("action parameter missing"),
                    panAlias = extraData["panAlias"]
                            ?: throw BsOldIntegrationException("panAlias parameter missing"),
                    username = extraData["username"]
                            ?: throw BsOldIntegrationException("username parameter missing"),
                    password = extraData["password"]
                            ?: throw BsOldIntegrationException("password parameter missing"),
                    eventExtId = extraData["eventExtId"]
                            ?: throw BsOldIntegrationException("eventExtId parameter missing"),
                    amount = extraData["amount"]
                            ?: throw BsOldIntegrationException("amount parameter missing"),
                    currency = extraData["currency"]
                            ?: throw BsOldIntegrationException("currency parameter missing"),
                    kind = extraData["kind"]
                            ?: "creditcard",
                    creditCardData = creditCardData
            )
        }
    }
}