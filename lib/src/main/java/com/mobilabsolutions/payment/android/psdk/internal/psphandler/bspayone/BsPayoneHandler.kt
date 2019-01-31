package com.mobilabsolutions.payment.android.psdk.internal.psphandler.bspayone

import com.mobilabsolutions.payment.android.psdk.internal.api.backend.*
import com.mobilabsolutions.payment.android.psdk.internal.api.payone.*
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.psppaypal.PayPalRedirectHandler
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BsPayoneHandler @Inject constructor(
        private val bsPayoneApi: BsPayoneApi,
        private val mobilabApi: MobilabApi,
        private val payPalRedirectHandler: PayPalRedirectHandler

) {
    fun registerCreditCard(
            paymentMethodRegistrationResponse: PaymentMethodRegistrationResponse,
            creditCardData: CreditCardData)
            : Single<String> {

        val payoneSpecificData = paymentMethodRegistrationResponse.providerSpecificData!! as PayoneSpecificData

        val baseRequest =
                payoneSpecificData.let {
                    BsPayoneBaseRequest.instance(
                            paymentMethodRegistrationResponse.merchantId,
                            it.portalId,
                            it.apiVersion,
                            it.mode,
                            it.request,
                            it.responseType,
                            payoneSpecificData.hash,
                            "utf-8"
                    )
                }
        val request =
                payoneSpecificData.let {
                    BsPayoneVerifcationRequest(
                            baseRequest,
                            it.accountId,
                            creditCardData.number,
                            "C",
                            creditCardData.expiryDate,
                            creditCardData.cvv,
                            "yes"

                    )
                }
        return bsPayoneApi.registerCreditCard(request).map {
            when(it) {
                is BsPayoneVerificationSuccessResponse -> {
                    val updatePaymentAliasRequest = UpdatePaymentAliasRequest(paymentMethodRegistrationResponse.panAlias, it.cardAlias)
                    mobilabApi.updatePaymentMethodAlias(updatePaymentAliasRequest).blockingAwait()
                    it.cardAlias
                }
                is BsPayoneVerificationErrorResponse -> throw BsPayoneErrorHandler.handleError(it)
                is BsPayoneVerificationInvalidResponse -> throw BsPayoneErrorHandler.handleError(it)
                else -> throw RuntimeException("Unknown responsewhen trying to register credit card: $it")
            }
        }
    }

    fun handlePayPalRedirectRequest(redirectUrl : String) : Single<PayPalRedirectHandler.RedirectResult> {
        return payPalRedirectHandler.handlePayPalRedirect(redirectUrl)
    }
}