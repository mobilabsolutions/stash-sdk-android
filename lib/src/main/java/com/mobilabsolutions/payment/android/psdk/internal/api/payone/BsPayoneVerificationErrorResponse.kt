package com.mobilabsolutions.payment.android.psdk.internal.api.payone

import com.google.gson.annotations.SerializedName

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class BsPayoneVerificationErrorResponse(
        override val status : String,
        @SerializedName("errorcode")
        val errorCode : Int,
        @SerializedName("errormessage")
        val errorMessage : String,
        @SerializedName("customermessage")
        val customerMessage : String?
) : BsPayoneVerificationBaseResponse()