package com.mobilabsolutions.payment.sample.data.entities

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */

enum class PaymentType(val sdkValue: String) {
    CREDIT_CARD("credit_card"),
    SEPA("sepa"),
    PAYPAL("paypal");

    companion object {
        fun fromVapianoValue(value: String): PaymentType = PaymentType.values().first { it.sdkValue == value }
    }
}


enum class CreditCardType(val sdkValue: String) {
    VISA("VISA"),
    MASTER_CARD("MASTERCARD");

    companion object {
        fun fromVapianoValue(value: String): CreditCardType? = CreditCardType.values().firstOrNull { it.sdkValue.equals(value, ignoreCase = true) }
    }
}