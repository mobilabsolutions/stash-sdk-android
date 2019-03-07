package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import com.mobilabsolutions.payment.android.psdk.integration.bspayone.pspapi.*
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.*
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasUpdateRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.psppaypal.PayPalRedirectHandler
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BsPayoneHandler @Inject constructor(
        private val bsPayoneApi: BsPayoneApi,
        private val mobilabApi: MobilabApi,
        private val mobilabApiV2: MobilabApiV2

) {
    fun registerCreditCard(
            aliasId : String,
            bsPayoneCreditCardRegistrationRequest: BsPayoneCreditCardRegistrationRequest,
            creditCardData: CreditCardData)
            : Single<String> {

        val baseRequest =
                bsPayoneCreditCardRegistrationRequest.let {
                    BsPayoneBaseRequest.instance(
                            it.merchantId,
                            it.portalId,
                            it.apiVersion,
                            it.mode,
                            it.request,
                            it.responseType,
                            it.hash,
                            "utf-8"
                    )
                }
        val request =
                bsPayoneCreditCardRegistrationRequest.let {
                    BsPayoneVerifcationRequest(
                            baseRequest,
                            it.accountId,
                            creditCardData.number,
                            "V",
                            creditCardData.expiryDate,
                            creditCardData.cvv,
                            "yes"

                    )
                }
//        return bsPayoneApi.executePayoneRequest(request).map {
//            when(it) {
//                is BsPayoneVerificationSuccessResponse -> {
//                    val updatePaymentAliasRequest = UpdatePaymentAliasRequest(paymentMethodRegistrationResponse.panAlias, it.cardAlias)
//                    mobilabApi.updatePaymentMethodAlias(updatePaymentAliasRequest).blockingAwait()
//                    it.cardAlias
//                }
//                is BsPayoneVerificationErrorResponse -> throw BsPayoneErrorHandler.handleError(it)
//                is BsPayoneVerificationInvalidResponse -> throw BsPayoneErrorHandler.handleError(it)
//                else -> throw RuntimeException("Unknown responsewhen trying to register credit card: $it")
//            }
//        }

        return bsPayoneApi.executePayoneRequestGet(request.toMap()).map {
            when(it) {
                is BsPayoneVerificationSuccessResponse -> {
                    val updatePaymentAliasRequest = UpdatePaymentAliasRequest(aliasId, it.cardAlias)
                    mobilabApiV2.updateAlias(aliasId, AliasUpdateRequest(it.cardAlias)).blockingGet()
                    it.cardAlias
                }
                is BsPayoneVerificationErrorResponse -> throw BsPayoneErrorHandler.handleError(it)
                is BsPayoneVerificationInvalidResponse -> throw BsPayoneErrorHandler.handleError(it)
                else -> throw RuntimeException("Unknown response when trying to register credit card: $it")
            }
        }
    }

//    fun handlePayPalRedirectRequest(redirectUrl : String) : Single<PayPalRedirectHandler.RedirectResult> {
//        return payPalRedirectHandler.handlePayPalRedirect(redirectUrl)
//    }
}