package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.pspapi.*
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApi
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApiV2
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasExtra
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasUpdateRequest
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.SepaConfig
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
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
    /**
     * We use this just for testing so we don't hit bspayone api rate limit, which seems to be quite low
     */
    val mockResponse = true

    fun registerCreditCard(
            aliasId: String,
            bsPayoneRegistrationRequest: BsPayoneRegistrationRequest,
            creditCardData: CreditCardData)
            : Single<String> {

        val baseRequest =
                bsPayoneRegistrationRequest.let {
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
                bsPayoneRegistrationRequest.let {
                    BsPayoneCreditCardVerifcationRequest(
                            baseRequest,
                            it.accountId,
                            creditCardData.number,
                            "V",
                            creditCardData.expiryDate,
                            creditCardData.cvv,
                            "yes"

                    )
                }

        return if (mockResponse) {
            val mockCardAlias = "MockCreditCardAlias"
            mobilabApiV2.updateAlias(aliasId, AliasUpdateRequest(
                    mockCardAlias,
                    AliasExtra(paymentMethod = "CC")
            )).blockingAwait()
            Single.just(aliasId)
        } else {
            bsPayoneApi.executePayoneRequestGet(request.toMap()).map {
                when (it) {
                    is BsPayoneVerificationSuccessResponse -> {
                        mobilabApiV2.updateAlias(aliasId, AliasUpdateRequest(
                                it.cardAlias,
                                AliasExtra(paymentMethod = PaymentMethodType.CREDITCARD.name)
                        )).blockingAwait()
                        aliasId
                    }
                    is BsPayoneVerificationErrorResponse -> throw BsPayoneErrorHandler.handleError(it)
                    is BsPayoneVerificationInvalidResponse -> throw BsPayoneErrorHandler.handleError(it)
                    else -> throw RuntimeException("Unknown response when trying to register credit card: $it")
                }
            }
        }

    }

    fun registerSepa(
            aliasId: String,
            sepaData: SepaData,
            billingData: BillingData
    ): Single<String> {

        val sepaConfig = SepaConfig(
                iban = sepaData.iban,
                bic = sepaData.bic,
                name = billingData.firstName,
                lastname = billingData.lastName,
                street = billingData.address1,
                zip = billingData.zip,
                city = billingData.city,
                country = billingData.country
        )
        return mobilabApiV2.updateAlias(
                aliasId,
                AliasUpdateRequest(
                        extra = AliasExtra(sepaConfig = sepaConfig, paymentMethod = PaymentMethodType.SEPA.name)
                )
        ).andThen(Single.just(aliasId))
    }

//    fun handlePayPalRedirectRequest(redirectUrl : String) : Single<PayPalRedirectHandler.RedirectResult> {
//        return payPalRedirectHandler.handlePayPalRedirect(redirectUrl)
//    }
}