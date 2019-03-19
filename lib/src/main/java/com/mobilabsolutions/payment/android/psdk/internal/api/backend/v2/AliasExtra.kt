package com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2

import com.google.gson.annotations.SerializedName


data class AliasExtra(
        @SerializedName("ccConfig")
        val creditCardConfig: CreditCardConfig? = null,
        val sepaConfig: SepaConfig? = null
)

data class CreditCardConfig(
        val ccExpiry: String,
        val ccMask: String,
        val ccType: String,
        val email: String,
        val ibanMaskval: String
)

data class SepaConfig(
        val iban : String? = null,
        val bic : String? = null,
        val name : String? = null,
        val lastname : String? = null,
        val street : String? = null,
        val zip : String? = null,
        val city : String? = null,
        val country : String? = null
)