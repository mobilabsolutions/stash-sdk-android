package com.mobilabsolutions.payment.android.psdk.model

import org.threeten.bp.LocalDate

/**
 * This class models data needed to register a credit card as a payment method
 * @author [Ugi](ugi@mobilabsolutions.com)
 */
class CreditCardData(
    val number: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val cvv: String,
    val billingData: BillingData? = null) {
    companion object {

        internal val CREDIT_CARD_NUMBER = "CREDIT_CARD_NUMBER"
        internal val EXPIRY_DATE = "EXPIRY_DATE"
        internal val CVV = "CVV"
    }

    class Builder() {
        var number : String? = null
        var expiryMonth : Int? = null
        var expiryYear : Int? = null
        var cvv : String? = null
        var billingData : BillingData? = null

        fun setNumber(number: String): Builder {
            this.number = number
            return this
        }
        fun setExpiryMonth(expiryMonth: Int): Builder {
            this.expiryMonth = expiryMonth
            return this
        }
        fun setCvv(cvv: String): Builder {
            this.cvv = cvv
            return this
        }
        fun setBillingData(billingData: BillingData): Builder {
            this.billingData = billingData
            return this
        }

        fun build() : CreditCardData {
            return CreditCardData(
                number!!,
                expiryMonth!!,
                expiryYear!!,
                cvv!!,
                billingData!!)
        }


    }
}
