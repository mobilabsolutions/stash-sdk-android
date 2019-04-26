package com.mobilabsolutions.payment.sample.network

import com.mobilabsolutions.payment.sample.network.request.CreatePaymentMethodRequest
import com.mobilabsolutions.payment.sample.network.response.CreatePaymentMethodResponse
import com.mobilabsolutions.payment.sample.network.response.CreateUserResponse
import com.mobilabsolutions.payment.sample.network.response.PaymentMethodListResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
interface SampleMerchantService {

    // todo dummy api calls. replace them with the real ones once the backend service's been deployed.

    @POST("/user")
    fun createUser(): Call<CreateUserResponse>

    @POST("/createPaymentMethod")
    fun createPaymentMethod(@Body request: CreatePaymentMethodRequest): Call<CreatePaymentMethodResponse>

    @DELETE("/deletePaymentMethod/{Payment-Method-Id}")
    fun deletePaymentMethod(@Path("Payment-Method-Id") paymentMethodId: String): Call<ResponseBody>

    @GET("/getPaymentMethods/{User-Id}")
    fun getPaymentMethods(@Path("User-Id") userId: String): Call<PaymentMethodListResponse>
}