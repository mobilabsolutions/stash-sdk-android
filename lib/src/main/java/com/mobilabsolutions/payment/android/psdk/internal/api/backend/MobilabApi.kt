package com.mobilabsolutions.payment.android.psdk.internal.api.backend

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT

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

    @HTTP(method = "DELETE", path = "v1/remove/creditcard", hasBody = true)
    fun deleteCreditCard(@Body cardAlias: RemoveAliasRequest): Completable

    @HTTP(method = "DELETE", path = "v1/remove/sepa", hasBody = true)
    fun deleteSepa(@Body paymentAlias: RemoveAliasRequest): Completable

    @PUT("v1/update/panalias")
    fun updatePaymentMethodAlias(@Body updatePaymentAliasRequest: UpdatePaymentAliasRequest): Completable

    @POST("v1/payment/creditcard")
    fun executePaymentWithCreditCardAlias(
            @Body paymentWithAliasRequest: PaymentWithAliasRequest
    ) : Single<SuccessResponse<PaymentWithAliasResponse>>

    @POST("v1/payment/sepa")
    fun executePaymentWithSepaAlias(
            @Body paymentWithAliasRequest: PaymentWithAliasRequest
    ) : Single<SuccessResponse<PaymentWithAliasResponse>>

    @POST("v1/payment/creditcard")
    fun executePaypalPayment(
            @Body paymentWithPayPalRequest: PaymentWithPayPalRequest
    ) : Single<SuccessResponse<PaymentWithPayPalResponse>>

    @POST("v1/paypal/callback")
    fun reportPayPalResult(
            @Body payPalConfirmationRequest: PayPalConfirmationRequest
    ) : Completable


}