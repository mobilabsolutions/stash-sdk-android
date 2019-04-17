package com.mobilabsolutions.payment.android.psdk.model

import org.threeten.bp.LocalDate

/**
 * This class models data needed to register a credit card as a payment method
 * @author [Ugi](ugi@mobilabsolutions.com)
 */
class CreditCardData(val number: String, val expiryDate: LocalDate, val cvv: String, val holder: String, additionalData : Map<String, String> = emptyMap()) {
    companion object {
        @JvmStatic
        fun create(number: String, expiryDate: LocalDate, cvv: String, holder: String) : CreditCardData {
            return CreditCardData(number, expiryDate, cvv, holder)
        }

        val CREDIT_CARD_NUMBER = "CREDIT_CARD_NUMBER"
        val EXPIRY_DATE = "EXPIRY_DATE"
        val CVV = "CVV"

    }
}

