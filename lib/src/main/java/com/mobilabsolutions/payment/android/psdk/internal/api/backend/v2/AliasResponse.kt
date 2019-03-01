package com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class AliasResponse(
        val aliasId: String,
        val extra : AliasExtra
)

data class AliasExtra(
        val ccExpiry : String,
        val ccMask : String,
        val ccType : String,
        val email : String,
        val ibanMaskval : String,
        val pspExtra : Map<String, String>
)