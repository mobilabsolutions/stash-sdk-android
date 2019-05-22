package com.mobilabsolutions.payment.android.psdk.internal

import android.annotation.SuppressLint
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
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.SepaRegistrationRequest
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
        private val integrations: Map<@JvmSuppressWildcards Integration, @JvmSuppressWildcards Set<@JvmSuppressWildcards PaymentMethodType>>,
        private val uiRequestHandler: UiRequestHandler,
        private val context: Context,
        private val idempotencyManager: IdempotencyManager
) {

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
                        .filter { it.value.contains(PaymentMethodType.CC) }
                        .keys
                        .first(),
                idempotencyKey
        )
    }

    @SuppressLint("NewApi")
    fun handleRegisterCreditCard(
        creditCardData: CreditCardData,
        billingData: BillingData = BillingData(),
        additionalUIData: Map<String, String>,
        chosenIntegration: Integration,
        idempotencyKey: String
    ): Single<PaymentMethodAlias> {

        billingData.country = additionalUIData.getOrNull(BillingData.COUNTRY)
        billingData.firstName = additionalUIData.getOrNull(BillingData.FIRST_NAME)
        billingData.lastName = additionalUIData.getOrNull(BillingData.LAST_NAME)

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
        return handleRegisterSepa(
                sepaData,
                billingData,
                additionalUIData,
                integrations
                        .filter { it.value.contains(PaymentMethodType.SEPA) }
                        .keys
                        .first(),
                idempotencyKey)
    }

    @SuppressLint("NewApi")
    fun handleRegisterSepa(
            sepaData: SepaData,
            billingData: BillingData,
            additionalUIData: Map<String, String> = emptyMap(),
            chosenIntegration: Integration,
            idempotencyKey: String): Single<PaymentMethodAlias> {

            billingData.country = additionalUIData.getOrNull(BillingData.COUNTRY)
            billingData.firstName = additionalUIData.getOrNull(SepaData.FIRST_NAME)
            billingData.lastName = additionalUIData.getOrNull(SepaData.LAST_NAME)


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

    fun registerCreditCardUsingUIComponent(activity: Activity?, chosenIntegration: Integration, idempotencyKey: String, requestId: Int): Single<PaymentMethodAlias> {
        return uiRequestHandler.handleCreditCardMethodEntryRequest(
                activity,
                chosenIntegration,
                PaymentMethodType.CC,
                requestId
        ).flatMap { (creditCardData, additionalUIData) ->
            handleRegisterCreditCard(creditCardData = creditCardData, additionalUIData = additionalUIData, idempotencyKey = idempotencyKey)
        }
    }

    fun registerSepaUsingUIComponent(activity: Activity?, chosenIntegration: Integration, idempotencyKey: String, requestId: Int): Single<PaymentMethodAlias> {
        return uiRequestHandler.handleSepaMethodEntryRequest(
                activity,
                chosenIntegration,
                PaymentMethodType.SEPA,
                requestId
        ).flatMap { (sepaData, additionalUIData) ->
            handleRegisterSepa(sepaData = sepaData, additionalUIData = additionalUIData, idempotencyKey = idempotencyKey)
        }
    }

    fun registerCreditCardUsingUIComponent(activity: Activity?, idempotencyKey: String, requestId: Int): Single<PaymentMethodAlias> {
        val chosenIntegration = integrations.filter {
            it.value.contains(PaymentMethodType.CC)
        }.keys.first()
        return registerCreditCardUsingUIComponent(activity, chosenIntegration, idempotencyKey, requestId)
    }

    fun registerSepaUsingUIComponent(activity: Activity?, idempotencyKey: String, requestId: Int): Single<PaymentMethodAlias> {
        val chosenIntegration = integrations.filter {
            it.value.contains(PaymentMethodType.SEPA)
        }.keys.first()
        return registerSepaUsingUIComponent(activity, chosenIntegration, idempotencyKey, requestId)
    }

    fun getAvailablePaymentMethods(): Set<PaymentMethodType> {
        return integrations.values.flatMap { it }.toSet()
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

        val chosenIntegration = integrations.filter {
            it.value.contains(PaymentMethodType.PAYPAL)
        }.keys.first()

        return idempotencyManager.verifyIdempotencyAndContinue(idempotencyKey, PaymentMethodType.PAYPAL) {
            chosenIntegration.getPreparationData(PaymentMethodType.PAYPAL).flatMap { preparationData ->
                mobilabApiV2.createAlias(chosenIntegration.identifier, idempotencyKey, preparationData)
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

private inline fun <reified KEY, reified VALUE> Map<KEY, VALUE>.getOrNull(key: String): VALUE? {
    return if (!containsKey(key as KEY)) {
        null
    } else {
        get(key) as VALUE
    }
}