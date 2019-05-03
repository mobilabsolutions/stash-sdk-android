package com.mobilabsolutions.payment.android.psdk.integration.adyen

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.adyen.checkout.core.CheckoutException
import com.adyen.checkout.core.PaymentSetupParameters
import com.adyen.checkout.core.StartPaymentParameters
import com.adyen.checkout.core.card.Card
import com.adyen.checkout.core.card.Cards
import com.adyen.checkout.core.handler.PaymentSetupParametersHandler
import com.adyen.checkout.core.handler.StartPaymentParametersHandler
import com.adyen.checkout.core.internal.*
import com.adyen.checkout.core.internal.model.PaymentInitiation
import com.adyen.checkout.core.internal.model.PaymentSessionImpl
import com.adyen.checkout.core.internal.persistence.PaymentRepository
import com.adyen.checkout.core.internal.persistence.PaymentSessionEntity
import com.adyen.checkout.core.model.CardDetails
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApiV2
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasExtra
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasUpdateRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodDefinition
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.lang.reflect.Constructor
import java.util.*
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class AdyenIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {
    override val identifier = "ADYEN"

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var mobilabApiV2: MobilabApiV2

    companion object : IntegrationCompanion {
        var integration: AdyenIntegration? = null

        override fun create(): IntegrationInitialization {
            return object : IntegrationInitialization {
                override fun initializedOrNull(): Integration? {
                    return integration
                }

                override fun initialize(paymentSdkComponent: PaymentSdkComponent, url: String): Integration {
                    if (integration == null) {
                        if (url.isEmpty()) {
                            integration = AdyenIntegration(paymentSdkComponent)
                        } else {
                            throw RuntimeException("Adyen doesn't support custom endpoint url")
                        }
                    }
                    return integration as Integration
                }
            }
        }
    }


    val adyenIntegrationComponent: AdyenIntegrationComponent


    init {
        adyenIntegrationComponent = DaggerAdyenIntegrationComponent.builder()
                .paymentSdkComponent(paymentSdkComponent)
                .build()

        adyenIntegrationComponent.inject(this)
    }

    val creditCardUIDefinition = PaymentMethodDefinition(
            methodId = "Adyen-CC",
            pspIdentifier = identifier,
            paymentMethodType = PaymentMethodType.CC
    )

    val sepaUIDefinition = PaymentMethodDefinition(
            methodId = "Adyen-Sepa",
            pspIdentifier = identifier,
            paymentMethodType = PaymentMethodType.SEPA
    )

    override fun getPreparationData(method: PaymentMethodType): Single<Map<String, String>> {
        return Single.create {
            val parameters = PaymentSetupParametersImpl(context)
            val paymentSetupParametersHandler = object : PaymentSetupParametersHandler {
                override fun onRequestPaymentSession(paymentSetupParameters: PaymentSetupParameters) {
                    it.onSuccess(
                            mapOf(
                                    "token" to paymentSetupParameters.sdkToken,
                                    "channel" to "Android",
                                    "returnUrl" to "app://"
                            )
                    )
                }

                override fun onError(error: CheckoutException) {
                    it.onError(error) //TODO Map exception into one of 4 categories
                }

            }
            paymentSetupParametersHandler.onRequestPaymentSession(parameters)
        }

    }

    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {


        val singleResult = Single.create<String> {
            val standardizedData = registrationRequest.standardizedData
            val additionalData = registrationRequest.additionalData


            val paymentSessionString = additionalData.extraData["paymentSession"]
                    ?: throw RuntimeException("Missing payment session")
            val decodedPayemntSessionDebug = String(Base64.getDecoder().decode(paymentSessionString))
            Timber.d("Decoded : $decodedPayemntSessionDebug")
            val paymentSession = PaymentSessionImpl.decode(paymentSessionString)

            val paymentSessionUuid = UUID.randomUUID().toString()

            val paymentSessionEntity = PaymentSessionEntity()
            paymentSessionEntity.uuid = paymentSessionUuid
            paymentSessionEntity.paymentSession = paymentSession
            paymentSessionEntity.generationTime = paymentSession.getGenerationTime()

            PaymentRepository.getInstance(context).insertPaymentSessionEntity(paymentSessionEntity)

            val constructor = paymentReferenceImplStringConstructor()
            constructor.isAccessible = true
            val paymentReferenceImpl = constructor.newInstance(paymentSessionUuid)

            val handlerConstructor = paymentHandlerImplConstructor()
            handlerConstructor.isAccessible = true
            val paymentHandlerImpl = handlerConstructor.newInstance(context.applicationContext, paymentSessionEntity, null)
            PaymentHandlerStore.getInstance().storePaymentHandler(paymentReferenceImpl, paymentHandlerImpl)
            val startPaymentParametersImpl = StartPaymentParametersImpl(paymentReferenceImpl, paymentSession)

            val handler = object : StartPaymentParametersHandler {
                override fun onPaymentInitialized(startPaymentParameters: StartPaymentParameters) {
                    Timber.d("Got payment parameters")
                    it.onSuccess("123")
                }

                override fun onError(error: CheckoutException) {
                    Timber.e("Error")
                    it.onError(error)
                }

            }
            handler.onPaymentInitialized(startPaymentParametersImpl)
            Timber.d("Payment methods: ${paymentSession.paymentMethods.joinToString { it.name }}")
            val visaPaymentMethod = paymentSession.paymentMethodImpls.filter { it.name == "VISA" }.first()
            val inputDetails = visaPaymentMethod.inputDetails


            val publicKey = paymentSession.publicKey!!
            val card = Card.Builder()
                    .setNumber("4111111111111111")
                    .setExpiryDate(10, 2020)
                    .setSecurityCode("737")
                    .build()
            val encryptedCard = Cards.ENCRYPTOR.encryptFields(card, paymentSession.generationTime, publicKey).call();
            val visaCardDetails = CardDetails.Builder()
                    .setHolderName("Holder Holderman")
                    .setEncryptedCardNumber(encryptedCard.encryptedNumber)
                    .setEncryptedExpiryMonth(encryptedCard.encryptedExpiryMonth)
                    .setEncryptedExpiryYear(encryptedCard.encryptedExpiryYear)
                    .setEncryptedSecurityCode(encryptedCard.encryptedSecurityCode)
                    .build()

            val paymentInitiation = PaymentInitiation.Builder(
                    paymentSession.paymentData, visaPaymentMethod.paymentMethodData)
                    .setPaymentMethodDetails(visaCardDetails)
                    .build()

            val paymentInitiationCallable = CheckoutApi
                    .getInstance(context.applicationContext as Application)
                    .initiatePayment(paymentSession, paymentInitiation)
            val paymentInitiationResult = Single.fromCallable {
                paymentInitiationCallable.call()
            }.subscribeOn(Schedulers.io()).blockingGet()

            Timber.d("Payment init complete")
//        val paymentMethod = paymentSession.paymentMethods.filter { it.n }
            mobilabApiV2.updateAlias(registrationRequest.standardizedData.aliasId , AliasUpdateRequest(
                    extra = AliasExtra(
                            paymentMethod = PaymentMethodType.CC.name,
                            payload = paymentInitiationResult.completeFields!!.payload

                    )
            )).subscribeOn(Schedulers.io()).blockingAwait()
            Timber.d("Payment init complete")

        }.subscribeOn(AndroidSchedulers.mainThread())


        return singleResult

//        val paymentSession = PaymentSessionImpl.decode(paymentSessionString)
//
//        val paymentSessionUuid = UUID.randomUUID().toString()
//
//        val paymentSessionEntity = PaymentSessionEntity()
//        paymentSessionEntity.uuid = paymentSessionUuid
//        paymentSessionEntity.paymentSession = paymentSession
//        paymentSessionEntity.generationTime = paymentSession.getGenerationTime()
//
//        PaymentRepository.getInstance(context).insertPaymentSessionEntity(paymentSessionEntity)
//        val paymentReference = PaymentReferenceImpl(paymentSessionUuid)
//
//        val paymentHandler = PaymentHandlerImpl(context.applicationContext, paymentSessionEntity, null)
//        PaymentHandlerStore.getInstance().storePaymentHandler(paymentReference, paymentHandler)
//        TODO()
//        return when (standardizedData) {
//            is CreditCardRegistrationRequest -> {
//
//            }
//            is SepaRegistrationRequest -> {
//
//            }
//            else -> {
//                throw RuntimeException("Unsupported payment method")
//            }
//        }
    }

    override fun getSupportedPaymentMethodDefinitions(): List<PaymentMethodDefinition> {
        return listOf(creditCardUIDefinition, sepaUIDefinition)
    }

    override fun handlePaymentMethodEntryRequest(activity: AppCompatActivity, paymentMethodDefinition: PaymentMethodDefinition): Single<Map<String, String>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
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
