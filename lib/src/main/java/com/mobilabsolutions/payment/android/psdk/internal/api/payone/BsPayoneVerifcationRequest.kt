package com.mobilabsolutions.payment.android.psdk.internal.api.payone

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate
import java.time.LocalDateTime

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class BsPayoneVerifcationRequest(
        @Transient
        val baseRequest: BsPayoneBaseRequest,
        @SerializedName("aid")
        val accountId : String,
        @SerializedName("cardpan")
        val cardPan : String,
        @SerializedName("cardtype")
        val cardType : String,
        @SerializedName("cardexpiredate")//TODO add custom expiry date serializer
        val cardExpireDate : LocalDate,
        @SerializedName("cardcvc2")
        val cardCvc : String,
        @SerializedName("storecarddata")
        val storeCardData : String = "yes"

) : BsPayoneBaseRequest(baseRequest)