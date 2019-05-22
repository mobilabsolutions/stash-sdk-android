package com.mobilabsolutions.payment.android.psdk.integration.adyen

import android.app.Application
import com.adyen.checkout.core.CheckoutException
import com.adyen.checkout.core.card.Card
import com.adyen.checkout.core.card.Cards
/* ktlint-disable no-wildcard-imports */
import com.adyen.checkout.core.internal.*
import com.adyen.checkout.core.internal.model.PaymentInitiation
import com.adyen.checkout.core.internal.model.PaymentInitiationResponse
import com.adyen.checkout.core.internal.model.PaymentMethodImpl
import com.adyen.checkout.core.internal.model.PaymentSessionImpl
import com.adyen.checkout.core.internal.persistence.PaymentRepository
import com.adyen.checkout.core.internal.persistence.PaymentSessionEntity
import com.adyen.checkout.core.model.CardDetails
import com.adyen.checkout.ui.internal.card.CardCheckoutMethodFactory
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.exceptions.base.OtherException
import com.mobilabsolutions.payment.android.psdk.exceptions.base.ValidationException
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApiV2
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasExtra
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasUpdateRequest
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.SepaConfig
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.CreditCardRegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.SepaRegistrationRequest
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.lang.reflect.Constructor
/* ktlint-disable no-wildcard-imports */
import java.util.*
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class AdyenHandler @Inject constructor(
    private val mobilabApiV2: MobilabApiV2,
    val application: Application
) {
    val PAYMENT_SESSION = "paymentSession"

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

        val singleResult = Single.create<String> {

            val billingData = creditCardRegistrationRequest.billingData
            val creditCardData = creditCardRegistrationRequest.creditCardData

            // Payment session received from the SDK backend
            val paymentSessionString = additionalData.extraData[PAYMENT_SESSION]
                    ?: throw OtherException("Missing payment session")

            // Here we start accesing the flow that CheckoutController.handlePaymentSessionResponse(...) would follow
            val paymentSession = PaymentSessionImpl.decode(paymentSessionString)

            // CheckoutController.handlePaymentSessionResponse... createPaymentReference
            val paymentSessionUuid = UUID.randomUUID().toString()

            val paymentSessionEntity = PaymentSessionEntity()
            paymentSessionEntity.uuid = paymentSessionUuid
            paymentSessionEntity.paymentSession = paymentSession
            paymentSessionEntity.generationTime = paymentSession.generationTime

            PaymentRepository.getInstance(application).insertPaymentSessionEntity(paymentSessionEntity)

            // Retrieve the payment reference implementation constructor and instantiate reference
            val constructor = paymentReferenceImplStringConstructor()
            constructor.isAccessible = true
            val paymentReferenceImpl = constructor.newInstance(paymentSessionUuid)

            // Retrieve the payment handler constructor and instantiate handler
            val handlerConstructor = paymentHandlerImplConstructor()
            handlerConstructor.isAccessible = true
            val paymentHandlerImpl = handlerConstructor.newInstance(application.applicationContext, paymentSessionEntity, null)
            PaymentHandlerStore.getInstance().storePaymentHandler(paymentReferenceImpl, paymentHandlerImpl)
            // At this point we are done with creating and storing a payment reference.
            // The rest of the calls of the handlePaymentSessionresponse are just communicating the reference
            // back to the caller, which we already obtained

            // Payment session will hold several payment methods, but these are specific VISA, Mastercard, etc. methods.
            // While we could decode the card number and select a specific payment method, this is actually not how
            // Adyen SDK does it itself.
            // Adyen SDK creates a "Card" payment method (valid for all supported cards) and sends that
            // So we will do the same here
            val cardCheckoutMethodFactory = CardCheckoutMethodFactory(application)
            val cardCheckoutMethod = cardCheckoutMethodFactory.initCheckoutMethods(paymentSession).call()
            val resolvedPaymentMethod = cardCheckoutMethod[0].paymentMethod as PaymentMethodImpl

            // Now we need to use Adyens Client Side Encryption to encrypt our credit card data
            val publicKey = paymentSession.publicKey!!
            val card = Card.Builder()
                    .setNumber(creditCardData.number)
                    .setExpiryDate(creditCardData.expiryDate.monthValue, creditCardData.expiryDate.year)
                    .setSecurityCode(creditCardData.cvv)
                    .build()
            val encryptedCard = Cards.ENCRYPTOR.encryptFields(card, paymentSession.generationTime, publicKey).call()
            val creditCardDetails = CardDetails.Builder()
                    .setHolderName(creditCardData.holder)
                    .setEncryptedCardNumber(encryptedCard.encryptedNumber)
                    .setEncryptedExpiryMonth(encryptedCard.encryptedExpiryMonth)
                    .setEncryptedExpiryYear(encryptedCard.encryptedExpiryYear)
                    .setEncryptedSecurityCode(encryptedCard.encryptedSecurityCode)
                    .build()
            // Now we have prepared everything we need to perform PaymentController.startPayment(...)
            // Since this is again tied to lifecycle, we will skip using observers and execute network call
            // directly
            val paymentInitiation = PaymentInitiation.Builder(
                    paymentSession.paymentData, resolvedPaymentMethod.paymentMethodData)
                    .setPaymentMethodDetails(creditCardDetails)
                    .build()
            // Prepare the call
            val paymentInitiationCallable = CheckoutApi
                    .getInstance(application.applicationContext as Application)
                    .initiatePayment(paymentSession, paymentInitiation)
            // Execute the call. If everything wasfine we will get "COMPLETED" type
            val paymentInitiationResult = Single.fromCallable {
                paymentInitiationCallable.call()
            }.subscribeOn(Schedulers.io()).blockingGet()

            when (paymentInitiationResult.type) {
                PaymentInitiationResponse.Type.COMPLETE -> {
                    // We call the SDK backend to deliver the payload, also because Adyen will not report
                    // if the credit card number or cvv/cvc was invalid, we rely on backend to return that
                    // information in the exchange call, as they will get an exception when trying to execute
                    // a payment
                    mobilabApiV2.updateAlias(creditCardRegistrationRequest.aliasId, AliasUpdateRequest(
                            extra = AliasExtra(
                                    paymentMethod = PaymentMethodType.CC.name,
                                    payload = paymentInitiationResult.completeFields!!.payload,
                                    personalData = creditCardRegistrationRequest.billingData

                            )
                    )).subscribeOn(Schedulers.io()).blockingAwait()
                    it.onSuccess(creditCardRegistrationRequest.aliasId)
                }
                PaymentInitiationResponse.Type.ERROR -> {
                    it.onError(OtherException(
                            message = paymentInitiationResult.errorFields?.errorMessage ?: "Unknown Adyen error"
                    ))
                }
                PaymentInitiationResponse.Type.VALIDATION -> {
                    it.onError(ValidationException(
                            message = paymentInitiationResult.errorFields?.errorMessage ?: "Unknown Adyen validation error"
                    ))
                }
                else -> {
                    Timber.w("Unhandled Adyen response ${paymentInitiationResult.type.name}")
                }
            }
            // Some of these calls check if they are running on main thread as they expect to use
            // observers tied to the lifecycle. Since all calls are internal and rather quick we can
            // run everything on the main thread except the network calls
        }.subscribeOn(AndroidSchedulers.mainThread())
        return singleResult
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
                name = sepaData.holder,
                lastname = billingData.lastName,
                street = billingData.address1,
                zip = billingData.zip,
                city = billingData.city,
                country = billingData.country
        )
        return mobilabApiV2.updateAlias(
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
                val parameters = PaymentSetupParametersImpl(application)
                val result = mapOf(
                        "token" to parameters.sdkToken,
                        "channel" to "Android",
                        "returnUrl" to "app://" // We're not supporting 3ds at the moment, so return URL is never used
                )
                it.onSuccess(result)
            } catch (exception: CheckoutException) {
                // This should rarely happen as it is actually just a device fingerprint
                it.onError(OtherException("Generating token failed", originalException = exception))
            }
        }
    }

    private fun paymentReferenceImplStringConstructor(): Constructor<PaymentReferenceImpl> {
        return PaymentReferenceImpl::class.java.declaredConstructors
                .filter {
                    it.parameterTypes.contains(String::class.java)
                }.first() as Constructor<PaymentReferenceImpl>
    }

    private fun paymentHandlerImplConstructor(): Constructor<PaymentHandlerImpl> {
        return PaymentHandlerImpl::class.java.declaredConstructors
                .filter {
                    it.parameterTypes.contains(Application::class.java)
                }.first() as Constructor<PaymentHandlerImpl>
    }
}