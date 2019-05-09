package com.mobilabsolutions.payment.android.psdk.internal

import android.app.Activity
import android.content.Context
import com.mobilabsolutions.payment.android.psdk.PaymentMethodAlias
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.exceptions.ExceptionMapper
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApiV2
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.CreditCardRegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.PayPalRegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.PspIdentifier
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.SepaRegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodDefinition
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.UiRequestHandler
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.util.Random
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PspCoordinator @Inject constructor(
    private val mobilabApiV2: MobilabApiV2,
    private val exceptionMapper: ExceptionMapper,
    private val integrations: Set<@JvmSuppressWildcards Integration>,
    private val uiRequestHandler: UiRequestHandler,
    private val context: Context,
    private val idempotencyManager: IdempotencyManager
) {

    companion object {
        private const val BRAINTREE_PSP_NAME = "BRAINTREE"
    }

    fun handleRegisterCreditCard(
        creditCardData: CreditCardData,
        billingData: BillingData = BillingData(),
        additionalUIData: Map<String, String> = emptyMap(),
        idempotencyKey: String
    ): Single<PaymentMethodAlias> {
        return handleRegisterCreditCard(
                creditCardData,
                billingData,
                additionalUIData,
                integrations
                        .filter { it.supportsPaymentMethods(PaymentMethodType.CC) }
                        .first()
                        .identifier,
                idempotencyKey
        )
    }

    fun handleRegisterCreditCard(
        creditCardData: CreditCardData,
        billingData: BillingData = BillingData(),
        additionalUIData: Map<String, String>,
        chosenPsp: PspIdentifier,
        idempotencyKey: String
    ): Single<PaymentMethodAlias> {

        val chosenIntegration = integrations.first { it.identifier == chosenPsp }

        return idempotencyManager.verifyIdempotencyAndContinue(idempotencyKey, PaymentMethodType.CC) {
            chosenIntegration.getPreparationData(PaymentMethodType.CC).flatMap { preparationData ->
                mobilabApiV2.createAlias(chosenIntegration.identifier, idempotencyKey, preparationData)
                        .subscribeOn(Schedulers.io())
                        .processErrors()
                        .flatMap {

                            val standardizedData = CreditCardRegistrationRequest(creditCardData = creditCardData, billingData = billingData, aliasId = it.aliasId)
                            val additionalData = AdditionalRegistrationData(it.pspExtra + additionalUIData)
                            val registrationRequest = RegistrationRequest(standardizedData, additionalData)

                            val pspAliasSingle = chosenIntegration.handleRegistrationRequest(registrationRequest)

                            pspAliasSingle.map { alias ->
                                PaymentMethodAlias(alias, PaymentMethodType.CC)
                            }
                        }
            }
        }
    }

    fun handleRegisterSepa(sepaData: SepaData, billingData: BillingData = BillingData(), additionalUIData: Map<String, String> = emptyMap(), idempotencyKey: String): Single<PaymentMethodAlias> {
        return handleRegisterSepa(sepaData, billingData, additionalUIData, integrations.filter { it.supportsPaymentMethods(PaymentMethodType.SEPA) }.first().identifier, idempotencyKey)
    }

    fun handleRegisterSepa(sepaData: SepaData, billingData: BillingData, additionalUIData: Map<String, String> = emptyMap(), chosenPsp: PspIdentifier, idempotencyKey: String): Single<PaymentMethodAlias> {
        if (additionalUIData.containsKey(BillingData.COUNTRY)) {
            billingData.country = additionalUIData.getValue(BillingData.COUNTRY)
        }
        val chosenIntegration = integrations.filter { it.identifier == chosenPsp }.first()

        // TODO validate
        return idempotencyManager.verifyIdempotencyAndContinue(idempotencyKey, PaymentMethodType.SEPA) {
            chosenIntegration.getPreparationData(PaymentMethodType.SEPA).flatMap { preparationData ->
                mobilabApiV2.createAlias(chosenIntegration.identifier, idempotencyKey, preparationData)
                        .subscribeOn(Schedulers.io())
                        .flatMap {

                            val standardizedData = SepaRegistrationRequest(sepaData = sepaData, billingData = billingData, aliasId = it.aliasId)
                            val additionalData = AdditionalRegistrationData(it.pspExtra + additionalUIData)
                            val registrationRequest = RegistrationRequest(standardizedData, additionalData)

                            chosenIntegration.handleRegistrationRequest(registrationRequest)
                                    .map {
                                        PaymentMethodAlias(it, PaymentMethodType.SEPA)
                                    }
                        }
            }
        }
    }

    //
    // ------- UI Component handling ------------
    //

    fun registerCreditCardUsingUIComponent(activity: Activity?, paymentMethodDefinition: PaymentMethodDefinition, idempotencyKey: String, requestId: Int): Single<PaymentMethodAlias> {
        val chosenIntegration = integrations.filter { it.identifier == paymentMethodDefinition.pspIdentifier }.first()
        return uiRequestHandler.handleCreditCardMethodEntryRequest(
                activity,
                chosenIntegration,
                paymentMethodDefinition,
                requestId
        ).flatMap { (creditCardData, additionalUIData) ->
            handleRegisterCreditCard(creditCardData = creditCardData, additionalUIData = additionalUIData, idempotencyKey = idempotencyKey)
        }
    }

    fun registerSepaUsingUIComponent(activity: Activity?, paymentMethodDefinition: PaymentMethodDefinition, idempotencyKey: String, requestId: Int): Single<PaymentMethodAlias> {
        val chosenIntegration = integrations.filter { it.identifier == paymentMethodDefinition.pspIdentifier }.first()
        return uiRequestHandler.handleSepaMethodEntryRequest(
                activity,
                chosenIntegration,
                paymentMethodDefinition,
                requestId
        ).flatMap { (sepaData, additionalUIData) ->
            handleRegisterSepa(sepaData = sepaData, additionalUIData = additionalUIData, idempotencyKey = idempotencyKey)
        }
    }

    fun registerCreditCardUsingUIComponent(activity: Activity?, idempotencyKey: String, requestId: Int): Single<PaymentMethodAlias> {
        val selectedPaymentMethodDefinition = integrations.flatMap {
            it.getSupportedPaymentMethodDefinitions().filter { it.paymentMethodType == PaymentMethodType.CC }
        }.first()
        return registerCreditCardUsingUIComponent(activity, selectedPaymentMethodDefinition, idempotencyKey, requestId)
    }

    fun registerSepaUsingUIComponent(activity: Activity?, idempotencyKey: String, requestId: Int): Single<PaymentMethodAlias> {
        val selectedPaymentMethodDefinition = integrations.flatMap {
            it.getSupportedPaymentMethodDefinitions().filter { it.paymentMethodType == PaymentMethodType.SEPA }
        }.first()
        return registerSepaUsingUIComponent(activity, selectedPaymentMethodDefinition, idempotencyKey, requestId)
    }

    fun getAvailablePaymentMethods(): Set<PaymentMethodType> {
        return integrations.flatMap { it.getSupportedPaymentMethodDefinitions().map { it.paymentMethodType } }.toSet()
    }

    private fun askUserToChoosePaymentMethod(activity: Activity?, requestId: Int): Single<PaymentMethodType> {
        return uiRequestHandler.askUserToChosePaymentMethod(activity, requestId)
    }

    fun handleRegisterPaymentMethodUsingUi(activity: Activity?, specificPaymentMethodType: PaymentMethodType?, idempotencyKey: String): Single<PaymentMethodAlias> {
        val requestId = Random().nextInt(Int.MAX_VALUE)
        val resolvedPaymentMethodType = if (specificPaymentMethodType != null) {
            Single.just(specificPaymentMethodType)
        } else {
            askUserToChoosePaymentMethod(activity, requestId)
        }
        return resolvedPaymentMethodType.flatMap {
            when (it) {
                PaymentMethodType.PAYPAL -> registerPayPalUsingUIComponent(activity, idempotencyKey, requestId)
                PaymentMethodType.CC -> registerCreditCardUsingUIComponent(activity, idempotencyKey, requestId)
                PaymentMethodType.SEPA -> registerSepaUsingUIComponent(activity, idempotencyKey, requestId)
            }
        }.onErrorResumeNext {
            if (it is UiRequestHandler.EntryCancelled) {
                handleRegisterPaymentMethodUsingUi(activity, specificPaymentMethodType, idempotencyKey)
            } else {
                Single.error(it)
            }
        }
    }

    private fun registerPayPalUsingUIComponent(activity: Activity?, idempotencyKey: String, requestId: Int): Single<PaymentMethodAlias> {
        val chosenIntegration = integrations.filter { it.identifier == BRAINTREE_PSP_NAME }.first()
        val backendImplemented = true

        val selectedPaymentMethodDefinition = integrations.flatMap {
            it.getSupportedPaymentMethodDefinitions().filter { it.paymentMethodType == PaymentMethodType.PAYPAL }
        }.first()

        return idempotencyManager.verifyIdempotencyAndContinue(idempotencyKey, PaymentMethodType.PAYPAL) {
            chosenIntegration.getPreparationData(PaymentMethodType.PAYPAL).flatMap { preparationData ->
                mobilabApiV2.createAlias(BRAINTREE_PSP_NAME, idempotencyKey, preparationData)
                        .subscribeOn(Schedulers.io())
                        .flatMap { aliasResponse ->
                            uiRequestHandler.handlePaypalMethodEntryRequest(
                                    activity,
                                    chosenIntegration,
                                    AdditionalRegistrationData(aliasResponse.pspExtra),
                                    requestId)
                                    .flatMap {
                                        val additionalData = AdditionalRegistrationData(it)
                                        val standardizedData = PayPalRegistrationRequest(aliasResponse.aliasId)
                                        val registrationRequest = RegistrationRequest(standardizedData, additionalData)
                                        chosenIntegration.handleRegistrationRequest(registrationRequest)
                                    }
                        }.map {
                            PaymentMethodAlias(it, PaymentMethodType.PAYPAL)
                        }
            }
        }
    }

    private fun <T> Single<T>.processErrors(): Single<T> {
        return onErrorResumeNext {
            when (it) {
                is HttpException -> Single.error(exceptionMapper.mapError(it))
                else -> Single.error(RuntimeException("Unknown exception ${it.message}"))
            }
        }
    }
}