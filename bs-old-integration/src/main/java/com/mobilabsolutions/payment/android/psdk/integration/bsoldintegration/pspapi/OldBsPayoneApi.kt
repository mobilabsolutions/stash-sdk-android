package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.pspapi

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface OldBsPayoneApi {

    @POST("services/XmlApi")
    @Headers("Content-Type: application/soap+xml; charset=UTF-8")
    fun registerCreditCard(
            @Header("Authorization") authorization : String,
            @Body bsPayonePaymentRequest: BsPayonePaymentRequest)
    : Single<BsPayonePaymentResponse>


}