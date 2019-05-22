package com.mobilabsolutions.payment.android.psdk

import com.google.gson.annotations.SerializedName

enum class PaymentMethodType {
    @SerializedName("CC")
    CREDIT_CARD,
    SEPA,
    PAYPAL
}