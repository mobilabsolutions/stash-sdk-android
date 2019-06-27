package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import com.mobilabsolutions.payment.android.psdk.CreditCardTypeWithRegex
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.exceptions.registration.UnknownException
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.pspapi.BsPayoneApi
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.pspapi.BsPayoneBaseRequest
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.pspapi.BsPayoneCreditCardVerifcationRequest
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.pspapi.BsPayoneVerificationErrorResponse
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.pspapi.BsPayoneVerificationInvalidResponse
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.pspapi.BsPayoneVerificationSuccessResponse
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApiV2
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasExtra
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasUpdateRequest
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.CreditCardConfig
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.SepaConfig
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.CreditCardRegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.SepaRegistrationRequest
import com.mobilabsolutions.payment.android.util.withLastDayOfMonth
import io.reactivex.Single
import org.threeten.bp.LocalDate
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BsPayoneHandler @Inject constructor(
    private val bsPayoneApi: BsPayoneApi,
    private val mobilabApiV2: MobilabApiV2

) {
    /**
     * We use this just for testing so we don't hit bspayone api rate limit, which seems to be quite low
     */
    val mockResponse = false

    fun registerCreditCard(
        creditCardRegistrationRequest: CreditCardRegistrationRequest,
        bsPayoneRegistrationRequest: BsPayoneRegistrationRequest
    ): Single<String> {

        val STORE_CARD_DATA = "yes"
        val ENCODING = "utf-8"

        val aliasId = creditCardRegistrationRequest.aliasId
        val creditCardData = creditCardRegistrationRequest.creditCardData
        val generalCardType = CreditCardTypeWithRegex.resolveCreditCardType(creditCardData.number)
        val bsPayoneType = BsPayoneCardType.valueOf(generalCardType.name).bsPayoneType

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
                    ENCODING
                )
            }
        val request =
            bsPayoneRegistrationRequest.let {
                BsPayoneCreditCardVerifcationRequest(
                    baseRequest,
                    it.accountId,
                    creditCardData.number,
                    bsPayoneType,
                    LocalDate.of(creditCardData.expiryYear, creditCardData.expiryMonth, 1).withLastDayOfMonth(),
                    creditCardData.cvv,
                    STORE_CARD_DATA

                )
            }

        return if (mockResponse) {
            val mockCardAlias = "MockCreditCardAlias"
            mobilabApiV2.updateAlias(aliasId, AliasUpdateRequest(
                mockCardAlias,
                AliasExtra(
                    paymentMethod = "CC",
                    creditCardConfig = CreditCardConfig(
                        ccExpiry = creditCardData.expiryMonth.toString() + "/" + creditCardData.expiryYear,
                        ccMask = bsPayoneType + "-" + creditCardData.number.takeLast(4),
                        ccType = bsPayoneType,
                        ccHolderName = creditCardData.billingData?.fullName()
                    )
                )
            )).blockingAwait()
            Single.just(aliasId)
        } else {
            bsPayoneApi.executePayoneRequestGet(request.toMap()).map {
                when (it) {
                    is BsPayoneVerificationSuccessResponse -> {
                        mobilabApiV2.updateAlias(aliasId, AliasUpdateRequest(
                            it.cardAlias,
                            AliasExtra(
                                paymentMethod = PaymentMethodType.CC.name,
                                personalData = creditCardRegistrationRequest.billingData,
                                creditCardConfig = CreditCardConfig(
                                    ccExpiry = creditCardData.expiryMonth.toString() + "/" + creditCardData.expiryYear,
                                    ccMask = bsPayoneType + "-" + creditCardData.number.takeLast(4),
                                    ccType = bsPayoneType,
                                    ccHolderName = creditCardData.billingData?.fullName()
                                )

                            )
                        )).blockingAwait()
                        aliasId
                    }
                    is BsPayoneVerificationErrorResponse -> throw BsPayoneErrorHandler.handleError(it)
                    is BsPayoneVerificationInvalidResponse -> throw BsPayoneErrorHandler.handleError(it)
                    else -> throw UnknownException("Unknown response when trying to register the credit card: $it")
                }
            }
        }
    }

    fun registerSepa(
        sepaRegistrationRequest: SepaRegistrationRequest
    ): Single<String> {
        val aliasId = sepaRegistrationRequest.aliasId
        val sepaData = sepaRegistrationRequest.sepaData
        val billingData = sepaRegistrationRequest.billingData

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
                extra = AliasExtra(sepaConfig = sepaConfig, paymentMethod = PaymentMethodType.SEPA.name, personalData = billingData)
            )
        ).andThen(Single.just(aliasId))
    }

    enum class BsPayoneCardType(val cardTypeWithRegex: CreditCardTypeWithRegex, val bsPayoneType: String) {

        JCB(CreditCardTypeWithRegex.JCB, "J"),

        AMEX(CreditCardTypeWithRegex.AMEX, "A"),

        DINERS(CreditCardTypeWithRegex.DINERS, "D"),

        VISA(CreditCardTypeWithRegex.VISA, "V"),

        MAESTRO_13(CreditCardTypeWithRegex.MAESTRO_13, "O"),

        MAESTRO_15(CreditCardTypeWithRegex.MAESTRO_15, "O"),

        MASTER_CARD(CreditCardTypeWithRegex.MASTER_CARD, "M"),

        DISCOVER(CreditCardTypeWithRegex.DISCOVER, "D"),

        UNIONPAY_16(CreditCardTypeWithRegex.UNIONPAY_16, "P"),

        UNIONPAY_19(CreditCardTypeWithRegex.UNIONPAY_19, "P"),

        MAESTRO(CreditCardTypeWithRegex.MAESTRO, "O")
    }
}