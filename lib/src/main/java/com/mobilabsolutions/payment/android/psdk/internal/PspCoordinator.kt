package com.mobilabsolutions.payment.android.psdk.internal

import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.exceptions.backend.BackendExceptionMapper
import com.mobilabsolutions.payment.android.psdk.exceptions.validation.SepaValidationException
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.*
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.hypercharge.HyperchargeHandler
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.bspayone.BsPayoneHandler
import com.mobilabsolutions.payment.android.psdk.model.PaymentData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.iban4j.CountryCode
import org.iban4j.Iban
import retrofit2.HttpException
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PspCoordinator @Inject constructor(
        private val hyperchageHandler : HyperchargeHandler,
        private val bsPayoneHandler: BsPayoneHandler,
        private val mobilabApi: MobilabApi,
        private val paymentProvider:PaymentSdk.Provider,
        private val exceptionMapper: BackendExceptionMapper
) {



    fun handleRegisterCreditCard(
            creditCardData: CreditCardData): Single<String> {
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
                    when (paymentProvider) {
                        PaymentSdk.Provider.NEW_PAYONE -> bsPayoneHandler.registerCreditCard(
                                it, creditCardData
                        )
                        PaymentSdk.Provider.HYPERCHARGE -> hyperchageHandler.registerCreditCard(
                                it, creditCardData
                        )
                    }
                }



    }

    fun handleRegisterSepa(sepaData: SepaData): Single<String> {
        val paymentMethodRegistrationRequest = PaymentMethodRegistrationRequest()
        paymentMethodRegistrationRequest.accountData = sepaData
        paymentMethodRegistrationRequest.cardMask = "SEPA-${sepaData.iban?.substring(0 .. 5)}"
        paymentMethodRegistrationRequest.oneTimePayment = false

        //Hypercharge currenlty can support only german SEPA
        if (paymentProvider == PaymentSdk.Provider.HYPERCHARGE) {
            val iban = Iban.valueOf(sepaData.iban)
            if (iban.countryCode != CountryCode.DE) {
                throw SepaValidationException("Only German SEPA accounts are supported with Hypercharge provider")
            }
        }

        return mobilabApi.registerSepa(paymentMethodRegistrationRequest)
                .subscribeOn(Schedulers.io())
                .map { it.result }
                .flatMap {
                    when (paymentProvider) {
                        PaymentSdk.Provider.NEW_PAYONE -> Single.just(it.paymentAlias)
                        PaymentSdk.Provider.HYPERCHARGE -> hyperchageHandler.registerSepa(it, sepaData)
                    }
                }
    }

    //NOTE: This waws never tested and is only an initial implementation of psp managed paypal payment
    //It is not complete.
    fun handleExecutePaypalPayment(
            paymentData: PaymentData,
            billingData: BillingData
    ) : Single<String> {
        val paymentWithPaypalRequest = PaymentWithPayPalRequest(
                amount = paymentData.amount,
                currency = paymentData.currency!!,
                customerId = paymentData.customerId,
                reason = paymentData.reason!!,
                billingData = billingData
        )
        return mobilabApi.executePaypalPayment(paymentWithPaypalRequest)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    val mappedTransactionId = it.result.mappedTransactionId
                    when (paymentProvider) {
                        PaymentSdk.Provider.NEW_PAYONE -> bsPayoneHandler.handlePayPalRedirectRequest(it.result.redirectUrl)
                        PaymentSdk.Provider.HYPERCHARGE -> throw RuntimeException("Not supported at the moment")
                    }.map {
                        Pair(it, mappedTransactionId)
                    }
                }.flatMap {
                    val mappedTransactionId = it.second
                    val responseResult = it.first
                    mobilabApi.reportPayPalResult(
                            PayPalConfirmationRequest(
                                    mappedTransactionId,
                                    responseResult.redirectState.name.toLowerCase(),
                                    responseResult.code)
                    ).subscribeOn(Schedulers.io())
                            .andThen(Single.just(mappedTransactionId))
                }
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