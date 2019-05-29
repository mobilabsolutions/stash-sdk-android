package com.mobilabsolutions.payment.android.psdk.model

/**
 * This class models data needed to register a credit card as a payment method
 * @author [Ugi](ugi@mobilabsolutions.com)
 */
data class CreditCardData(
    val number: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val cvv: String,
    val billingData: BillingData? = null
) {
    companion object {

        val CREDIT_CARD_NUMBER = "CREDIT_CARD_NUMBER"
        val EXPIRY_MONTH = "EXPIRY_DATE"
        val EXPIRY_YEAR = "EXPIRY_YEAR"
        val CVV = "CVV"
    }

    class Builder {
        private var number: String? = null
        private var expiryMonth: Int? = null
        private var expiryYear: Int? = null
        private var cvv: String? = null
        private var billingData: BillingData? = null

        fun setNumber(number: String): Builder {
            this.number = number
            return this
        }
        fun setExpiryMonth(expiryMonth: Int): Builder {
            this.expiryMonth = expiryMonth
            return this
        }
        fun setExpiryYear(expiryYear: Int): Builder {
            this.expiryYear = expiryYear
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

        fun build(): CreditCardData {
            return CreditCardData(
                number!!,
                expiryMonth!!,
                expiryYear!!,
                cvv!!,
                billingData!!)
        }
    }
}
