package com.mobilabsolutions.payment.android.psdk

/**
 * Represents the payment method types the SDK currently supports
 */
enum class PaymentMethodType {
    /**
     * Credit card
     */
    CC,
    /**
     * SEPA bank account
     */
    SEPA,
    /**
     * PayPal account
     */
    PAYPAL
}