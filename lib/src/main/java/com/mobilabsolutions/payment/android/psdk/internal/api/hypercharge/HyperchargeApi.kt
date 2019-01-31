package com.mobilabsolutions.payment.android.psdk.internal.api.hypercharge

import io.reactivex.Single
import retrofit2.http.*

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface HyperchargeApi {
    @POST
    @Headers("Content-Type: application/xml")
    fun registerCreditCardAlias(@Url url : String,
                                @Body hyperchargeCreditCardPaymentRequest: HyperchargeCreditCardPaymentRequest) : Single<HyperchargePaymentResponse>

    @POST
    @Headers("Content-Type: application/xml")
    fun registerSepaAlias(@Url url : String,
                          @Body hyperchargeSepaPaymentRequest: HyperchargeSepaPaymentRequest
                          ): Single<HyperchargePaymentResponse>

}