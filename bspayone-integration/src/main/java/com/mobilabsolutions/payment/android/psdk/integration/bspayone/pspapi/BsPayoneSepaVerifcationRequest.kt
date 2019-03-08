package com.mobilabsolutions.payment.android.psdk.integration.bspayone.pspapi

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class BsPayoneSepaVerifcationRequest(
        @Transient
        val baseRequest: BsPayoneBaseRequest,
        @SerializedName("aid")
        val accountId: String,
        @SerializedName("iban")
        val iban : String

) : BsPayoneBaseRequest(baseRequest) {
    override fun toMap(): MutableMap<String, String> {
        var map = baseRequest.toMap()
        map.putAll(
                mapOf(
                        "aid" to accountId,
                        "iban" to iban

                )
        )



        return map
    }
}