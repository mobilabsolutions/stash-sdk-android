package com.mobilabsolutions.payment.android.psdk.internal

import com.mobilabsolutions.payment.android.psdk.exceptions.backend.BackendExceptionMapper
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApi
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApiV2
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.PaymentMethodRegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.*
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.hypercharge.HyperchargeHandler
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.PaymentData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PspCoordinator @Inject constructor(
        private val hyperchageHandler : HyperchargeHandler,
        private val mobilabApi: MobilabApi,
        private val mobilabApiV2 : MobilabApiV2,
        private val exceptionMapper: BackendExceptionMapper,
        private val integrations : Set<@JvmSuppressWildcards Integration>
) {


    fun handleRegisterCreditCard(
            creditCardData: CreditCardData): Single<String> {
        return handleRegisterCreditCard(creditCardData, integrations.first().identifier)
    }

    fun handleRegisterCreditCard(
            creditCardData: CreditCardData, chosenPsp : PspIdentifier): Single<String> {
        val chosenIntegration = integrations.filter { it.identifier == chosenPsp }.first()
        val paymentMethodRegistrationRequest = PaymentMethodRegistrationRequest()
        if (creditCardData.number.length < 16) {
            return Single.error(RuntimeException("Invalid card number length"))
        }
        paymentMethodRegistrationRequest.cardMask = creditCardData.number.substring(0..5)
        paymentMethodRegistrationRequest.oneTimePayment = false

        //TODO proper PSP type
        return mobilabApiV2.createAlias("BSPayone")
                .subscribeOn(Schedulers.io())
                .processErrors()
                .flatMap {

                    val standardizedData = CreditCardRegistrationRequest(creditCardData = creditCardData, aliasId = it.aliasId)
                    val additionalData = AdditionalRegistrationData(it.extra.pspExtra)
                    val registrationRequest = RegistrationRequest(standardizedData, additionalData)

                    val pspAliasSingle = chosenIntegration.handleRegistrationRequest(registrationRequest)

                    pspAliasSingle
//                    when (paymentProvider) {
////                        PaymentSdk.Provider.NEW_PAYONE -> bsPayoneHandler.registerCreditCard(
////                                it, creditCardData
////                        )
//                        PaymentSdk.Provider.HYPERCHARGE -> hyperchageHandler.registerCreditCard(
//                                it, creditCardData
//                        )
//                    }
                }



    }

    fun handleRegisterSepa(sepaData: SepaData): Single<String> {
        return handleRegisterSepa(sepaData, integrations.first().identifier)
    }

    fun handleRegisterSepa(sepaData: SepaData, chosenPsp : PspIdentifier): Single<String> {
        val chosenIntegration = integrations.filter { it.identifier == chosenPsp }.first()
        val paymentMethodRegistrationRequest = PaymentMethodRegistrationRequest()
        paymentMethodRegistrationRequest.accountData = sepaData
        paymentMethodRegistrationRequest.cardMask = "SEPA-${sepaData.iban?.substring(0 .. 5)}"
        paymentMethodRegistrationRequest.oneTimePayment = false

        //Hypercharge currenlty can support only german SEPA
//        if (paymentProvider == PaymentSdk.Provider.HYPERCHARGE) {
//            val iban = Iban.valueOf(sepaData.iban)
//            if (iban.countryCode != CountryCode.DE) {
//                throw SepaValidationException("Only German SEPA accounts are supported with Hypercharge provider")
//            }
//        }

        //TODO proper psp strings
        return mobilabApiV2.createAlias("BSPayone")
                .subscribeOn(Schedulers.io())
                .flatMap {

                    val standardizedData = SepaRegistrationRequest(sepaData = sepaData, aliasId = it.aliasId)
                    val additionalData = AdditionalRegistrationData(it.extra.pspExtra)
                    val registrationRequest = RegistrationRequest(standardizedData, additionalData)

                    chosenIntegration.handleRegistrationRequest(registrationRequest)

//                    when (paymentProvider) {
////                        PaymentSdk.Provider.NEW_PAYONE -> Single.just(it.paymentAlias)
//                        PaymentSdk.Provider.HYPERCHARGE -> hyperchageHandler.registerSepa(it, sepaData)
//                    }
                }
    }

    //NOTE: This was never tested and is only an initial implementation of psp managed paypal payment
    //It is not complete.
    fun handleExecutePaypalPayment(
            paymentData: PaymentData,
            billingData: BillingData
    ) : Single<String> {
//        val paymentWithPaypalRequest = PaymentWithPayPalRequest(
//                amount = paymentData.amount,
//                currency = paymentData.currency!!,
//                customerId = paymentData.customerId,
//                reason = paymentData.reason!!,
//                billingData = billingData
//        )
//        return mobilabApi.executePaypalPayment(paymentWithPaypalRequest)
//                .subscribeOn(Schedulers.io())
//                .flatMap {
//                    val mappedTransactionId = it.result.mappedTransactionId
//                    when (paymentProvider) {
////                        PaymentSdk.Provider.NEW_PAYONE -> bsPayoneHandler.handlePayPalRedirectRequest(it.result.redirectUrl)
//                        PaymentSdk.Provider.HYPERCHARGE -> throw RuntimeException("Not supported at the moment")
//                    }.map {
//                        Pair(it, mappedTransactionId)
//                    }
//                }.flatMap {
//                    val mappedTransactionId = it.second
//                    val responseResult = it.first
//                    mobilabApi.reportPayPalResult(
//                            PayPalConfirmationRequest(
//                                    mappedTransactionId,
//                                    responseResult.redirectState.name.toLowerCase(),
//                                    responseResult.code)
//                    ).subscribeOn(Schedulers.io())
//                            .andThen(Single.just(mappedTransactionId))
//                }
        return Single.just("TODO")
    }

    fun <T> Single<T>.processErrors() : Single<T> {
        return onErrorResumeNext {
            when(it) {
                is HttpException -> Single.error(exceptionMapper.mapError(it))
                else -> Single.error(RuntimeException("Unknown exception ${it.message}"))
            }

        }
    }


}