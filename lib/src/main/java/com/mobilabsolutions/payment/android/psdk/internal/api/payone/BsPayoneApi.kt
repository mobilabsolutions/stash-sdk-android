package com.mobilabsolutions.payment.android.psdk.internal.api.payone

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface BsPayoneApi {
    @POST("/client-api")
    fun executePayoneRequest(@Body registrationRequest: BsPayoneVerifcationRequest) : Single<BsPayoneVerificationBaseResponse>

    @GET("/client-api")
    fun executePayoneRequestGet(@QueryMap queryMap : Map<String, String>) : Single<BsPayoneVerificationBaseResponse>

}