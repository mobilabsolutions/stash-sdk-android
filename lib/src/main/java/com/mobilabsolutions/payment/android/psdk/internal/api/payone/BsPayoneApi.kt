package com.mobilabsolutions.payment.android.psdk.internal.api.payone

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface BsPayoneApi {
    @POST("client-api/")
    fun registerCreditCard(@Body registrationRequest: BsPayoneVerifcationRequest) : Single<BsPayoneVerificationBaseResponse>

}