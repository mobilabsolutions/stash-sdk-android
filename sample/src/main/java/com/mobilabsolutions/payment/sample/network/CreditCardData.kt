package com.mobilabsolutions.payment.sample.network

import com.google.gson.annotations.SerializedName

data class CreditCardData(
    @SerializedName("ccExpiryMonth") val expiryMonth: String,
    @SerializedName("ccExpiryYear") val expiryYear: String,
    @SerializedName("ccType") val type: String,
    @SerializedName("ccMask") val mask: String
)