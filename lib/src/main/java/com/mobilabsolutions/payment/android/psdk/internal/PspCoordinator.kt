package com.mobilabsolutions.payment.android.psdk.internal

import android.app.Activity
import android.content.Context
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.exceptions.backend.BackendExceptionMapper
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApi
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApiV2
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.PaymentMethodRegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.*
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodDefinition
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.UiRequestHandler
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PspCoordinator @Inject constructor(
        private val mobilabApi: MobilabApi,
        private val mobilabApiV2: MobilabApiV2,
        private val exceptionMapper: BackendExceptionMapper,
        private val integrations: Set<@JvmSuppressWildcards Integration>,
        private val uiRequestHandler: UiRequestHandler,
        private val context: Context
) {
    val BRAINTREE_PSP_NAME = "BRAINTREE"


    fun handleRegisterCreditCardOld(
            creditCardData: CreditCardData): Single<String> {
        return handleRegisterCreditCardOld(creditCardData, integrations.first().identifier)
    }

    fun handleRegisterCreditCardOld(
            creditCardData: CreditCardData, chosenPsp: PspIdentifier): Single<String> {
        val chosenIntegration = integrations.filter { it.identifier == chosenPsp }.first()

        if (creditCardData.number.length < 16) {
            return Single.error(RuntimeException("Invalid card number length"))
        }
        val paymentMethodRegistrationRequest = PaymentMethodRegistrationRequest()
        if (creditCardData.number.length < 16) {
            return Single.error(RuntimeException("Invalid card number length"))
        }
        paymentMethodRegistrationRequest.cardMask = creditCardData.number?.substring(0..5)
        paymentMethodRegistrationRequest.oneTimePayment = false

        return mobilabApi.registerCreditCard(paymentMethodRegistrationRequest)
                .subscribeOn(Schedulers.io())
                .processErrors()
                .map { it.result }
                .flatMap {
                    //TODO temporary for validation, to be removed before going public
                    val standardizedData = CreditCardRegistrationRequest(creditCardData = creditCardData, aliasId = it.paymentAlias)
                    val mappedValues = mapOf(
                            "paymentAlias" to it.paymentAlias!!,
                            "url" to it.url!!,
                            "merchantId" to it.merchantId!!,
                            "action" to it.action!!,
                            "panAlias" to it.panAlias!!,
                            "username" to it.username!!,
                            "password" to it.password!!,
                            "eventExtId" to it.eventExtId!!,
                            "amount" to it.amount!!,
                            "currency" to it.currency!!
                    )
                    val additionalData = AdditionalRegistrationData(mappedValues)
                    val registrationRequest = RegistrationRequest(standardizedData, additionalData)

                    chosenIntegration.handleRegistrationRequest(registrationRequest)
                }

    }

    fun handleRegisterCreditCard(
            creditCardData: CreditCardData, billingData: BillingData = BillingData(),
            additionalUIData: Map<String, String> = emptyMap(), idempotencyKey: String): Single<String> {
        return handleRegisterCreditCard(
                creditCardData,
                billingData,
                additionalUIData,
                integrations
                        .filter { it.supportsPaymentMethods(PaymentMethodType.CREDITCARD) }
                        .first()
                        .identifier,
                idempotencyKey
        )
    }

    fun handleRegisterCreditCard(
            creditCardData: CreditCardData, billingData: BillingData = BillingData(),
            additionalUIData: Map<String, String>, chosenPsp: PspIdentifier, idempotencyKey : String): Single<String> {

        val chosenIntegration = integrations.filter { it.identifier == chosenPsp }.first()

        //TODO proper validation
        if (creditCardData.number.length < 16) {
            return Single.error(RuntimeException("Invalid card number length"))
        }

        return mobilabApiV2.createAlias(chosenIntegration.identifier, idempotencyKey)
                .subscribeOn(Schedulers.io())
                .processErrors()
                .flatMap {

                    val standardizedData = CreditCardRegistrationRequest(creditCardData = creditCardData, billingData = billingData, aliasId = it.aliasId)
                    val additionalData = AdditionalRegistrationData(it.pspExtra + additionalUIData)
                    val registrationRequest = RegistrationRequest(standardizedData, additionalData)

                    val pspAliasSingle = chosenIntegration.handleRegistrationRequest(registrationRequest)

                    pspAliasSingle
                }


    }

    fun handleRegisterSepa(sepaData: SepaData, billingData: BillingData = BillingData(), additionalUIData: Map<String, String> = emptyMap(), idempotencyKey: String): Single<String> {
        return handleRegisterSepa(sepaData, billingData, additionalUIData, integrations.filter { it.supportsPaymentMethods(PaymentMethodType.SEPA) }.first().identifier, idempotencyKey)
    }

    fun handleRegisterSepa(sepaData: SepaData, billingData: BillingData, additionalUIData: Map<String, String> = emptyMap(), chosenPsp: PspIdentifier, idempotencyKey: String): Single<String> {
        val chosenIntegration = integrations.filter { it.identifier == chosenPsp }.first()


        //TODO validate

        return mobilabApiV2.createAlias(chosenIntegration.identifier, idempotencyKey)
                .subscribeOn(Schedulers.io())
                .flatMap {

                    val standardizedData = SepaRegistrationRequest(sepaData = sepaData, billingData = billingData, aliasId = it.aliasId)
                    val additionalData = AdditionalRegistrationData(it.pspExtra + additionalUIData)
                    val registrationRequest = RegistrationRequest(standardizedData, additionalData)

                    chosenIntegration.handleRegistrationRequest(registrationRequest)

                }
    }

    //
    // ------- UI Component handling ------------
    //

    fun registerCreditCardUsingUIComponent(activity: Activity?, paymentMethodDefinition: PaymentMethodDefinition, idempotencyKey : String): Single<String> {
        val chosenIntegration = integrations.filter { it.identifier == paymentMethodDefinition.pspIdentifier }.first()
        return uiRequestHandler.handleCreditCardMethodEntryRequest(
                activity,
                chosenIntegration,
                paymentMethodDefinition
        ).flatMap { (creditCardData, additionalUIData) ->
            handleRegisterCreditCard(creditCardData = creditCardData, additionalUIData = additionalUIData, idempotencyKey = idempotencyKey)
        }

    }

    fun registerSepaUsingUIComponent(activity: Activity?, paymentMethodDefinition: PaymentMethodDefinition, idempotencyKey : String): Single<String> {
        val chosenIntegration = integrations.filter { it.identifier == paymentMethodDefinition.pspIdentifier }.first()
        return uiRequestHandler.handleSepaMethodEntryRequest(
                        activity,
                        chosenIntegration,
                        paymentMethodDefinition
                ).flatMap { (sepaData, additionalUIData) ->
                    handleRegisterSepa(sepaData = sepaData, additionalUIData = additionalUIData, idempotencyKey = idempotencyKey)

                }

    }

    fun registerCreditCardUsingUIComponent(activity: Activity?, idempotencyKey : String): Single<String> {
        val selectedPaymentMethodDefinition = integrations.flatMap {
            it.getSupportedPaymentMethodDefinitions().filter { it.paymentMethodType == PaymentMethodType.CREDITCARD }
        }.first()
        return registerCreditCardUsingUIComponent(activity, selectedPaymentMethodDefinition, idempotencyKey)
    }

    fun registerSepaUsingUIComponent(activity: Activity?, idempotencyKey : String): Single<String> {
        val selectedPaymentMethodDefinition = integrations.flatMap {
            it.getSupportedPaymentMethodDefinitions().filter { it.paymentMethodType == PaymentMethodType.SEPA }
        }.first()
        return registerSepaUsingUIComponent(activity, selectedPaymentMethodDefinition, idempotencyKey)
    }

    fun getAvailablePaymentMethods(): Set<PaymentMethodType> {
        return integrations.flatMap { it.getSupportedPaymentMethodDefinitions().map { it.paymentMethodType } }.toSet()
    }

    private fun askUserToChoosePaymentMethod(activity: Activity?): Single<PaymentMethodType> {
        return uiRequestHandler.askUserToChosePaymentMethod(activity, getAvailablePaymentMethods())
    }

    fun handleRegisterPaymentMethodUsingUi(activity: Activity?, specificPaymentMethodType: PaymentMethodType?, idempotencyKey: String): Single<String> {
        val resolvedPaymentMethodType = if (specificPaymentMethodType != null) {
            Single.just(specificPaymentMethodType)
        } else {
            askUserToChoosePaymentMethod(activity)
        }
        return resolvedPaymentMethodType.flatMap {
            when (it) {
                PaymentMethodType.PAYPAL -> registerPayPalUsingUIComponent(activity, idempotencyKey)
                PaymentMethodType.CREDITCARD -> registerCreditCardUsingUIComponent(activity, idempotencyKey)
                PaymentMethodType.SEPA -> registerSepaUsingUIComponent(activity, idempotencyKey)
            }
        }

    }

    private fun registerPayPalUsingUIComponent(activity: Activity?, idempotencyKey : String): Single<String> {
        val chosenIntegration = integrations.filter { it.identifier == BRAINTREE_PSP_NAME }.first()
        val backendImplemented = false

        val selectedPaymentMethodDefinition = integrations.flatMap {
            it.getSupportedPaymentMethodDefinitions().filter { it.paymentMethodType == PaymentMethodType.PAYPAL }
        }.first()
        if (backendImplemented) {
            return mobilabApiV2.createAlias(BRAINTREE_PSP_NAME, idempotencyKey)
                    .subscribeOn(Schedulers.io())
                    .flatMap { aliasResponse ->
                        uiRequestHandler.handlePaypalMethodEntryRequest(
                                activity,
                                chosenIntegration,
                                selectedPaymentMethodDefinition).flatMap {
                            val additionalData = AdditionalRegistrationData(it)
                            val standardizedData = PayPalRegistrationRequest(aliasResponse.aliasId)
                            val registrationRequest = RegistrationRequest(standardizedData, additionalData)
                            chosenIntegration.handleRegistrationRequest(registrationRequest)
                        }
                    }
        } else {
            return uiRequestHandler.handlePaypalMethodEntryRequest(
                    activity,
                    chosenIntegration,
                    selectedPaymentMethodDefinition
            ).flatMap {
                chosenIntegration.handleRegistrationRequest(RegistrationRequest(PayPalRegistrationRequest(it["nonce"]
                        ?: "PayPalNonce"), AdditionalRegistrationData(it)))
            }
        }

    }

    fun <T> Single<T>.processErrors(): Single<T> {
        return onErrorResumeNext {
            when (it) {
                is HttpException -> Single.error(exceptionMapper.mapError(it))
                else -> Single.error(RuntimeException("Unknown exception ${it.message}"))
            }

        }
    }


}