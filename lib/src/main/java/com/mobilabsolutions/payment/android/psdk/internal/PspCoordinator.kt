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
            creditCardData: CreditCardData, billingData: BillingData = BillingData(), additionalUIData: Map<String, String> = emptyMap()): Single<String> {
        return handleRegisterCreditCard(creditCardData, billingData, additionalUIData, integrations.first().identifier)
    }

    fun handleRegisterCreditCard(
            creditCardData: CreditCardData, billingData: BillingData = BillingData(), additionalUIData: Map<String, String>, chosenPsp: PspIdentifier): Single<String> {
        val chosenIntegration = integrations.filter { it.identifier == chosenPsp }.first()

        if (creditCardData.number.length < 16) {
            return Single.error(RuntimeException("Invalid card number length"))
        }

        return mobilabApiV2.createAlias(chosenIntegration.identifier)
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

    fun handleRegisterSepa(sepaData: SepaData, billingData: BillingData = BillingData(), additionalUIData: Map<String, String> = emptyMap()): Single<String> {
        return handleRegisterSepa(sepaData, billingData, additionalUIData, integrations.first().identifier)
    }

    fun handleRegisterSepa(sepaData: SepaData, billingData: BillingData, additionalUIData: Map<String, String> = emptyMap(), chosenPsp: PspIdentifier): Single<String> {
        val chosenIntegration = integrations.filter { it.identifier == chosenPsp }.first()

        return mobilabApiV2.createAlias(chosenIntegration.identifier)
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

    fun registerCreditCardUsingUIComponent(activity: Activity?, paymentMethodDefinition: PaymentMethodDefinition): Single<String> {
        val chosenIntegration = integrations.filter { it.identifier == paymentMethodDefinition.pspIdentifier }.first()
        val (creditCardData, additionalUIData) =
                uiRequestHandler.handleCreditCardMethodEntryRequest(
                        activity,
                        chosenIntegration,
                        paymentMethodDefinition
                )
        return handleRegisterCreditCard(creditCardData = creditCardData, additionalUIData = additionalUIData)

    }

    fun registerSepaUsingUIComponent(activity: Activity?, paymentMethodDefinition: PaymentMethodDefinition): Single<String> {
        val chosenIntegration = integrations.filter { it.identifier == paymentMethodDefinition.pspIdentifier }.first()
        val (sepaData, additionalUIData) =
                uiRequestHandler.handleSepadMethodEntryRequest(
                        activity,
                        chosenIntegration,
                        paymentMethodDefinition
                )
        return handleRegisterSepa(sepaData = sepaData, additionalUIData = additionalUIData)

    }

    fun registerCreditCardUsingUIComponent(activity: Activity?): Single<String> {
        val selectedPaymentMethodDefinition = integrations.flatMap {
            it.getSupportedPaymentMethodDefinitions().filter { it.paymentMethodType == PaymentMethodType.CREDITCARD }
        }.first()
        return registerCreditCardUsingUIComponent(activity, selectedPaymentMethodDefinition)
    }

    fun registerSepaUsingUIComponent(activity: Activity?): Single<String> {
        val selectedPaymentMethodDefinition = integrations.flatMap {
            it.getSupportedPaymentMethodDefinitions().filter { it.paymentMethodType == PaymentMethodType.SEPA }
        }.first()
        return registerSepaUsingUIComponent(activity, selectedPaymentMethodDefinition)
    }

    fun getAvailablePaymentMethods() : Set<PaymentMethodType> {
        return integrations.flatMap {  it.getSupportedPaymentMethodDefinitions().map {it.paymentMethodType}  }.toSet()
    }

    private fun askUserToChoosePaymentMethod(activity: Activity?): Single<PaymentMethodType> {
        return uiRequestHandler.askUserToChosePaymentMethod(activity, getAvailablePaymentMethods())
    }

    fun handleRegisterPaymentMethodUsingUi(activity: Activity?, specificPaymentMethodType: PaymentMethodType?): Single<String> {
        return if (specificPaymentMethodType != null) {
            when (specificPaymentMethodType) {
                PaymentMethodType.PAYPAL -> registerPayPalUsingUIComponent(activity)
                PaymentMethodType.CREDITCARD -> registerCreditCardUsingUIComponent(activity)
                PaymentMethodType.SEPA -> registerSepaUsingUIComponent(activity)
            }
        } else {
            askUserToChoosePaymentMethod(activity)
                    .flatMap {
                        when (it) {
                            PaymentMethodType.PAYPAL -> registerPayPalUsingUIComponent(activity)
                            PaymentMethodType.CREDITCARD -> registerCreditCardUsingUIComponent(activity)
                            PaymentMethodType.SEPA -> registerSepaUsingUIComponent(activity)
                        }

                    }
        }
    }

    private fun registerPayPalUsingUIComponent(activity : Activity?): Single<String> {
        val chosenIntegration = integrations.filter { it.identifier == BRAINTREE_PSP_NAME }.first()
        val backendImplemented = false
        return if (backendImplemented) {
            return mobilabApiV2.createAlias(BRAINTREE_PSP_NAME)
                    .subscribeOn(Schedulers.io())
                    .flatMap {

                        val standardizedData = PayPalRegistrationRequest(it.aliasId)
                        val registrationRequest = RegistrationRequest(standardizedData)
                        uiRequestHandler.hadlePaypalMethodEntryRequest(activity, registrationRequest)

                    }
        } else {
            chosenIntegration.handleRegistrationRequest(RegistrationRequest(PayPalRegistrationRequest("1")))
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