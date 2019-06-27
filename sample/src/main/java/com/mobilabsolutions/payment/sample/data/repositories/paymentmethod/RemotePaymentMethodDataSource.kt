package com.mobilabsolutions.payment.sample.data.repositories.paymentmethod

import com.mobilabsolutions.payment.sample.data.RetrofitRunner
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.data.entities.PaymentType
import com.mobilabsolutions.payment.sample.data.entities.Result
import com.mobilabsolutions.payment.sample.data.mappers.PaymentMethodListResponseToEntity
import com.mobilabsolutions.payment.sample.network.CreditCardAliasData
import com.mobilabsolutions.payment.sample.network.PayPalAliasData
import com.mobilabsolutions.payment.sample.network.SampleMerchantService
import com.mobilabsolutions.payment.sample.network.SepaAliasData
import com.mobilabsolutions.payment.sample.network.request.AuthorizePaymentRequest
import com.mobilabsolutions.payment.sample.network.request.CreatePaymentMethodRequest
import com.mobilabsolutions.payment.sample.network.response.AuthorizePaymentResponse
import com.mobilabsolutions.payment.sample.network.response.CreatePaymentMethodResponse
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
class RemotePaymentMethodDataSource @Inject constructor(
    private val retrofitRunner: RetrofitRunner,
    private val sampleMerchantService: SampleMerchantService,
    private val paymentMethodListResponseToEntity: PaymentMethodListResponseToEntity
) {
    suspend fun addPaymentMethod(userId: String, aliasId: String, paymentMethod: PaymentMethod): Result<CreatePaymentMethodResponse> {
        var request = CreatePaymentMethodRequest(
            aliasId = aliasId,
            type = paymentMethod._type,
            userId = userId
        )

        request = when (paymentMethod.type) {
            PaymentType.CC -> request.copy(
                creditCardAliasData = CreditCardAliasData(
                    type = paymentMethod._cardType,
                    mask = paymentMethod.mask,
                    expiryMonth = paymentMethod.expiryMonth,
                    expiryYear = paymentMethod.expiryYear
                )
            )
            PaymentType.SEPA -> request.copy(
                sepaAliasData = SepaAliasData(paymentMethod.iban)
            )
            PaymentType.PAY_PAL -> request.copy(
                payPalAliasData = PayPalAliasData(paymentMethod.email)
            )
            else -> request
        }

        return retrofitRunner.executeForServerResponse {
            sampleMerchantService.createPaymentMethod(request).execute()
        }
    }

    suspend fun deletePaymentMethod(paymentMethodId: String): Result<Unit> {
        return retrofitRunner.executeWithNoResult {
            sampleMerchantService.deletePaymentMethod(paymentMethodId).execute()
        }
    }

    suspend fun getPaymentMethods(userId: String): Result<List<PaymentMethod>> {
        return retrofitRunner.executeForResponse(paymentMethodListResponseToEntity) {
            sampleMerchantService.getPaymentMethods(userId).execute()
        }
    }

    suspend fun authorizePayment(authorizePaymentRequest: AuthorizePaymentRequest): Result<AuthorizePaymentResponse> {
        return retrofitRunner.executeForServerResponse {
            sampleMerchantService.authorizePayment(authorizePaymentRequest).execute()
        }
    }
}