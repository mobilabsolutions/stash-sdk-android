package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.oldbspayone

import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApi
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.PaymentMethodRegistrationResponse
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.UpdatePaymentAliasRequest
import com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.pspapi.BsPayonePaymentRequest
import com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.pspapi.OldBsPayoneApi
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.util.BSUtils
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class OldBsPayoneHandler @Inject constructor(private val oldBsPayoneApi: OldBsPayoneApi, private val mobilabApi: MobilabApi) {

    fun registerCreditCard(paymentMethodRegistrationResponse: PaymentMethodRegistrationResponse, creditCardData: CreditCardData): Single<String> {

        return oldBsPayoneApi.registerCreditCard(
                authorization = getAuthorizationString(paymentMethodRegistrationResponse),
                bsPayonePaymentRequest = BsPayonePaymentRequest
                        .fromPaymentMethodResponseWithCreditCardData(
                                paymentMethodRegistrationResponse,
                                creditCardData
                        )
        ).map {
            val responseCode = it.apiResponse?.response?.rc ?: -1
            when (responseCode) {
                -1 -> throw RuntimeException("Missing response code")//TODO specify error
                0 -> {
                    paymentMethodRegistrationResponse.paymentAlias
                }
                1343 -> {
                    mobilabApi.updatePaymentMethodAlias(
                            UpdatePaymentAliasRequest(
                                    it.apiResponse?.response?.creditCard?.panAlias.toString(),
                                    paymentMethodRegistrationResponse.paymentAlias
                            )
                    ).blockingAwait()
                    paymentMethodRegistrationResponse.paymentAlias
                }
                else -> throw RuntimeException("Unexpected response from BS Api")//TODO specify error
            }

        }
    }

    private fun getAuthorizationString(paymentMethodRegistrationResponse: PaymentMethodRegistrationResponse): String {
        return "Basic ${BSUtils.getBasicAuthString(paymentMethodRegistrationResponse.username, paymentMethodRegistrationResponse.password)}"
    }
}



