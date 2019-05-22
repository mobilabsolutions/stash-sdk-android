package com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2

import com.google.gson.annotations.SerializedName
import com.mobilabsolutions.payment.android.psdk.model.BillingData

data class AliasExtra(
    @SerializedName("ccConfig")
    val creditCardConfig: CreditCardConfig? = null,
    val sepaConfig: SepaConfig? = null,
    val paymentMethod: String,
    val payPalConfig: PayPalConfig? = null,
    val personalData: BillingData? = null,
    val payload: String? = null

)

data class CreditCardConfig(
    val ccExpiry: String,
    val ccMask: String,
    val ccType: String,
    val email: String
)

data class SepaConfig(
    val iban: String? = null,
    val bic: String? = null,
    val name: String? = null,
    val lastname: String? = null,
    val street: String? = null,
    val zip: String? = null,
    val city: String? = null,
    val country: String? = null
)

data class PayPalConfig(
    val nonce: String = "",
    val deviceData: String = ""
)

data class DeviceData(@SerializedName("correlation_id") val correlationId: String)