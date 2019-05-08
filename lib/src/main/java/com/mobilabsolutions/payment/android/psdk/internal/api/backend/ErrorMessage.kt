package com.mobilabsolutions.payment.android.psdk.internal.api.backend

import com.google.gson.annotations.SerializedName

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class ErrorMessage(
    @SerializedName("error_code") val code: Int,
    @SerializedName("error_description") val message: String,
    val providerDetails: String?
)