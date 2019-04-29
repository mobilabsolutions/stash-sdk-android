package com.mobilabsolutions.payment.android.psdk.internal.api.backend

import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasResponse
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasUpdateRequest
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface MobilabApi {

    @POST("v1/register/creditcard")
    fun registerCreditCard(
        @Body paymentMethodRegistrationRequest: PaymentMethodRegistrationRequest
    ): Single<SuccessResponse<PaymentMethodRegistrationResponse>>

    @POST("v1/register/sepa")
    fun registerSepa(
        @Body paymentMethodRegistrationRequest: PaymentMethodRegistrationRequest
    ): Single<SuccessResponse<PaymentMethodRegistrationResponse>>

    @PUT("v1/update/panalias")
    fun updatePaymentMethodAlias(@Body updatePaymentAliasRequest: UpdatePaymentAliasRequest): Completable

    @POST("v1/payment/creditcard")
    fun executePaypalPayment(
        @Body paymentWithPayPalRequest: PaymentWithPayPalRequest
    ): Single<SuccessResponse<PaymentWithPayPalResponse>>

    @POST("v1/paypal/callback")
    fun reportPayPalResult(
        @Body payPalConfirmationRequest: PayPalConfirmationRequest
    ): Completable
}

interface MobilabApiV2 {
    @POST("v1/alias")
    fun createAlias(@Header("PSP-Type") psp: String, @Header("Idempotent-Key") idempotencyKey: String, @Body dynamicPspConfig : Map<String,String>): Single<AliasResponse>

    @PUT("v1/alias/{aliasId}")
    fun updateAlias(@Path("aliasId") aliasId: String, @Body aliasUpdateRequest: AliasUpdateRequest): Completable
}