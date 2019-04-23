package com.mobilabsolutions.payment.sample.remote

import com.mobilabsolutions.payment.sample.remote.response.PaymentMethodResponse
import retrofit2.Call
import retrofit2.http.GET

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-04-2019.
 */
interface MerchantApi {
    @GET("/paymentMethods")
    fun getPaymentMethods(): Call<PaymentMethodResponse>
}