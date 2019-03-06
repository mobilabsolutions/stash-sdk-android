package com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2

import com.google.gson.annotations.SerializedName

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class AliasResponse(
        val aliasId: String,
        val extra : AliasExtra,
        @SerializedName("psp")
        val pspExtra : Map<String, String>
)

data class AliasExtra(
        val ccExpiry : String,
        val ccMask : String,
        val ccType : String,
        val email : String,
        val ibanMaskval : String
)