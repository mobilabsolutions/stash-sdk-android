package com.mobilabsolutions.payment.android.psdk.integration.bspayone.pspapi

import com.google.gson.annotations.SerializedName

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class BsPayoneVerificationInvalidResponse(
        override val status : String,
        @SerializedName("errorcode")
        val errorCode : Int,
        @SerializedName("errormessage")
        val errorMessage : String,
        @SerializedName("customermessage")
        val customerMessage : String?
) : BsPayoneVerificationBaseResponse()