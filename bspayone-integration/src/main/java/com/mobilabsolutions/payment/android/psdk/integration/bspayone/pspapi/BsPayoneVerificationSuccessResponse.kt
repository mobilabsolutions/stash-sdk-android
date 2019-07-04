/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk.integration.bspayone.pspapi

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class BsPayoneVerificationSuccessResponse(
    override val status: String,
    @SerializedName("pseudocardpan")
    val cardAlias: String,
    @SerializedName("truncatedcardpan")
    val truncatedCardPan: String,
    @SerializedName("cardtype")
    val cardType: String,
    @SerializedName("cardexpiredate")
    val cardExpiryDate: LocalDate

) : BsPayoneVerificationBaseResponse()