package com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2

import com.google.gson.annotations.SerializedName

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class AliasResponse(
    val aliasId: String,
    val extra: AliasExtra,
    @SerializedName("psp")
    val pspExtra: Map<String, String>
)
