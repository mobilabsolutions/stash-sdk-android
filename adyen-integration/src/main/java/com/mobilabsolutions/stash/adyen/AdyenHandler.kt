/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.adyen

import android.app.Application
import com.adyen.checkout.base.model.payments.response.Threeds2FingerprintAction
import com.adyen.checkout.core.exeption.CheckoutException
import com.adyen.checkout.cse.Card
import com.adyen.checkout.cse.internal.CardEncryptorImpl
import com.mobilabsolutions.stash.core.CreditCardTypeWithRegex
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.exceptions.base.OtherException
import com.mobilabsolutions.stash.core.internal.api.backend.MobilabApi
import com.mobilabsolutions.stash.core.internal.api.backend.v1.AliasExtra
import com.mobilabsolutions.stash.core.internal.api.backend.v1.AliasUpdateRequest
import com.mobilabsolutions.stash.core.internal.api.backend.v1.CreditCardConfig
import com.mobilabsolutions.stash.core.internal.api.backend.v1.SepaConfig
import com.mobilabsolutions.stash.core.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.stash.core.internal.psphandler.CreditCardRegistrationRequest
import com.mobilabsolutions.stash.core.internal.psphandler.SepaRegistrationRequest
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class AdyenHandler @Inject constructor(
    private val mobilabApi: MobilabApi,
    val application: Application
) {
    val PAYMENT_SESSION = "paymentSession"
    private val clientEncryptionKey = "clientEncryptionKey"

    fun registerCreditCard(
        creditCardRegistrationRequest: CreditCardRegistrationRequest,
        additionalData: AdditionalRegistrationData
    ): Single<String> {
        /*
        Adyen SDK API requires an activity to handle registration, as it is tightly coupled
        with lifecycle components, but to actually perform registration, application context is enough.
        Unfortunately some of the classes needed have private constructors so we needed to use reflecton
        to make them available, this means that we cannot easily bump up Adyen SDK versions without additional testing.
        On  the other hand it's reasonable to expect that Adyen will support current version of
        their SDK for a significant time period.
         */

        return Single.create<String> {
            val creditCardData = creditCardRegistrationRequest.creditCardData
            val publicKey = additionalData.extraData[clientEncryptionKey]
                    ?: error("No client encrypt key!")

            val card = Card.Builder()
                    .setNumber(creditCardData.number)
                    .setExpiryDate(creditCardData.expiryMonth, creditCardData.expiryYear)
                    .setSecurityCode(creditCardData.cvv)
                    .build()

            val cardEncryptor = CardEncryptorImpl()
            val encryptCard = cardEncryptor.encryptFields(card, publicKey)
            Timber.e("encryptCard: $encryptCard")

            val creditCardType = CreditCardTypeWithRegex.resolveCreditCardType(creditCardData.number)
            val creditCardTypeName = creditCardType.name

            mobilabApi.testupdateAlias(
                    creditCardRegistrationRequest.aliasId,
                    AliasUpdateRequest(
                            extra = AliasExtra(
                                    creditCardConfig = CreditCardConfig(
                                            ccExpiry = creditCardData.expiryMonth.toString() + "/" + creditCardData.expiryYear.toString().takeLast(2),
                                            ccMask = creditCardData.number.takeLast(4),
                                            ccType = creditCardTypeName,
                                            ccHolderName = creditCardData.billingData?.fullName(),
                                            encryptedCardNumber = encryptCard.encryptedNumber,
                                            encryptedExpiryMonth = encryptCard.encryptedExpiryMonth,
                                            encryptedExpiryYear = encryptCard.encryptedExpiryYear,
                                            encryptedSecurityCode = encryptCard.encryptedSecurityCode
                                    ),
                                    paymentMethod = PaymentMethodType.CC.name,
                                    personalData = creditCardRegistrationRequest.billingData

                            )
                    )
            )
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        Timber.e("onSuccess: $it")
                        val action = Threeds2FingerprintAction()
                        action.token = it.token
                        action.paymentData = it.paymentData
                        action.type = it.actionType

                        application.startActivity(ThreeDsHandleActivity.createIntent(
                                application, action,
                                creditCardRegistrationRequest.aliasId
                        ))
//                        threedsComponent.handleAction()
//                        application.applicationContext.startActivity(
//                                Intent(
//                                        application.applicationContext,
//                                        ThreeDsHandleActivity::class.java
//                                ))

                    }, {
                        Timber.e(it)
                    })

            // Payment session received from the SDK backend
            val paymentSessionString = additionalData.extraData[PAYMENT_SESSION]
                    ?: throw OtherException("Missing payment session")

            // Here we start accesing the flow that CheckoutController.handlePaymentSessionResponse(...) would follow
//            val paymentSession = PaymentSessionImpl.decode(paymentSessionString)
//
//            // CheckoutController.handlePaymentSessionResponse... createPaymentReference
//            val paymentSessionUuid = UUID.randomUUID().toString()
//
//            val paymentSessionEntity = PaymentSessionEntity()
//            paymentSessionEntity.uuid = paymentSessionUuid
//            paymentSessionEntity.paymentSession = paymentSession
//            paymentSessionEntity.generationTime = paymentSession.generationTime
//
//            PaymentRepository.getInstance(application).insertPaymentSessionEntity(paymentSessionEntity)
//
//            // Retrieve the payment reference implementation constructor and instantiate reference
//            val constructor = paymentReferenceImplStringConstructor()
//            constructor.isAccessible = true
//            val paymentReferenceImpl = constructor.newInstance(paymentSessionUuid)
//
//            // Retrieve the payment handler constructor and instantiate handler
//            val handlerConstructor = paymentHandlerImplConstructor()
//            handlerConstructor.isAccessible = true
//            val paymentHandlerImpl = handlerConstructor.newInstance(application.applicationContext, paymentSessionEntity, null)
//            PaymentHandlerStore.getInstance().storePaymentHandler(paymentReferenceImpl, paymentHandlerImpl)
//            // At this point we are done with creating and storing a payment reference.
//            // The rest of the calls of the handlePaymentSessionresponse are just communicating the reference
//            // back to the caller, which we already obtained
//
//            // Payment session will hold several payment methods, but these are specific VISA, Mastercard, etc. methods.
//            // While we could decode the card number and select a specific payment method, this is actually not how
//            // Adyen SDK does it itself.
//            // Adyen SDK creates a "Card" payment method (valid for all supported cards) and sends that
//            // So we will do the same here
//            val cardCheckoutMethodFactory = CardCheckoutMethodFactory(application)
//            val cardCheckoutMethod = cardCheckoutMethodFactory.initCheckoutMethods(paymentSession).call()
//            val resolvedPaymentMethod = cardCheckoutMethod.first { method ->
//                method.paymentMethod.type == "card"
//            }.paymentMethod as PaymentMethodImpl
//
//            // Now we need to use Adyens Client Side Encryption to encrypt our credit card data
//            val publishableKey = paymentSession.publicKey!!
//            val card = Card.Builder()
//                .setNumber(creditCardData.number)
//                .setExpiryDate(creditCardData.expiryMonth, creditCardData.expiryYear)
//                .setSecurityCode(creditCardData.cvv)
//                .build()
//            val encryptedCard = Cards.ENCRYPTOR.encryptFields(card, paymentSession.generationTime, publishableKey).call()
//            val creditCardDetails = CardDetails.Builder()
//                    .setHolderName(creditCardData.billingData?.fullName())
//                    .setEncryptedCardNumber(encryptedCard.encryptedNumber)
//                    .setEncryptedExpiryMonth(encryptedCard.encryptedExpiryMonth)
//                    .setEncryptedExpiryYear(encryptedCard.encryptedExpiryYear)
//                    .setEncryptedSecurityCode(encryptedCard.encryptedSecurityCode)
//                    .build()
//            // Now we have prepared everything we need to perform PaymentController.startPayment(...)
//            // Since this is again tied to lifecycle, we will skip using observers and execute network call
//            // directly
//            val paymentInitiation = PaymentInitiation.Builder(
//                paymentSession.paymentData, resolvedPaymentMethod.paymentMethodData)
//                .setPaymentMethodDetails(creditCardDetails)
//                .build()
//            // Prepare the call
//            val paymentInitiationCallable = CheckoutApi
//                .getInstance(application.applicationContext as Application)
//                .initiatePayment(paymentSession, paymentInitiation)
//            // Execute the call. If everything wasfine we will get "COMPLETED" type
//            val paymentInitiationResult = Single.fromCallable {
//                paymentInitiationCallable.call()
//            }.subscribeOn(Schedulers.io()).blockingGet()
//
//            when (paymentInitiationResult.type) {
//                PaymentInitiationResponse.Type.COMPLETE -> {
//                    // We call the SDK backend to deliver the payload, also because Adyen will not report
//                    // if the credit card number or cvv/cvc was invalid, we rely on backend to return that
//                    // information in the exchange call, as they will get an throwable when trying to execute
//                    // a payment
//                    val creditCardType = CreditCardTypeWithRegex.resolveCreditCardType(creditCardData.number)
//                    val creditCardTypeName = creditCardType.name
//                    mobilabApi.updateAlias(creditCardRegistrationRequest.aliasId, AliasUpdateRequest(
//                        extra = AliasExtra(
//                            creditCardConfig = CreditCardConfig(
//                                ccExpiry = creditCardData.expiryMonth.toString() + "/" + creditCardData.expiryYear.toString().takeLast(2),
//                                ccMask = creditCardData.number.takeLast(4),
//                                ccType = creditCardTypeName,
//                                ccHolderName = creditCardData.billingData?.fullName()
//                            ),
//                            paymentMethod = PaymentMethodType.CC.name,
//                            payload = paymentInitiationResult.completeFields!!.payload,
//                            personalData = creditCardRegistrationRequest.billingData
//
//                        )
//                    )).subscribeOn(Schedulers.io()).blockingAwait()
//                    it.onSuccess(creditCardRegistrationRequest.aliasId)
//                }
//                PaymentInitiationResponse.Type.ERROR -> {
//                    it.onError(OtherException(
//                        message = paymentInitiationResult.errorFields?.errorMessage
//                            ?: "Unknown Adyen error"
//                    ))
//                }
//                PaymentInitiationResponse.Type.VALIDATION -> {
//                    it.onError(ValidationException(
//                        message = paymentInitiationResult.errorFields?.errorMessage
//                            ?: "Unknown Adyen validation error"
//                    ))
//                }
//                else -> {
//                    Timber.w("Unhandled Adyen response ${paymentInitiationResult.type.name}")
//                }
//            }
//            // Some of these calls check if they are running on main thread as they expect to use
//            // observers tied to the lifecycle. Since all calls are internal and rather quick we can
//            // run everything on the main thread except the network calls
//        }.subscribeOn(AndroidSchedulers.mainThread())
        }
    }

    fun registerSepa(
        sepaRegistrationRequest: SepaRegistrationRequest
    ): Single<String> {
        val sepaData = sepaRegistrationRequest.sepaData
        val aliasId = sepaRegistrationRequest.aliasId
        val billingData = sepaRegistrationRequest.billingData

        val sepaConfig = SepaConfig(
                iban = sepaData.iban,
                bic = sepaData.bic,
                name = sepaData.billingData?.fullName(),
                lastname = billingData.lastName,
                street = billingData.address1,
                zip = billingData.zip,
                city = billingData.city,
                country = billingData.country
        )
        return mobilabApi.updateAlias(
                aliasId,
                AliasUpdateRequest(
                        extra = AliasExtra(sepaConfig = sepaConfig,
                                paymentMethod = PaymentMethodType.SEPA.name,
                                personalData = sepaRegistrationRequest.billingData
                        )
                )
        ).andThen(Single.just(aliasId))
    }

    fun getPreparationData(method: PaymentMethodType): Single<Map<String, String>> {
        // Doesn't matter what the method is, token should be returned
        return Single.create {
            try {
//                val parameters = PaymentSetupParametersImpl(application)
//                val result = mapOf(
//                        "token" to parameters.sdkToken,
//                        "channel" to "Android",
//                        "returnUrl" to "app://" // We're not supporting 3ds at the moment, so return URL is never used
//                )
                it.onSuccess(mapOf("test" to "test"))
            } catch (exception: CheckoutException) {
                // This should rarely happen as it is actually just a device fingerprint
                it.onError(OtherException("Generating token failed", originalException = exception))
            }
        }
    }

//    @Suppress("UNCHECKED_CAST")
//    private fun paymentReferenceImplStringConstructor(): Constructor<PaymentReferenceImpl> {
//        return PaymentReferenceImpl::class.java.declaredConstructors.first {
//            it.parameterTypes.contains(String::class.java)
//        } as Constructor<PaymentReferenceImpl>
//    }
//
//    @Suppress("UNCHECKED_CAST")
//    private fun paymentHandlerImplConstructor(): Constructor<PaymentHandlerImpl> {
//        return PaymentHandlerImpl::class.java.declaredConstructors.first {
//            it.parameterTypes.contains(Application::class.java)
//        } as Constructor<PaymentHandlerImpl>
//    }
}