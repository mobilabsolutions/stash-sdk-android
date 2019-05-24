package com.mobilabsolutions.payment.sample.data.entities

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */

enum class PaymentType(val sdkValue: String) {
    CREDIT_CARD("Credit Card"),
    SEPA("SEPA"),
    PAYPAL("PayPal");

    companion object {
        fun fromStringValue(value: String): PaymentType? = values().firstOrNull { it.sdkValue == value }
    }
}
