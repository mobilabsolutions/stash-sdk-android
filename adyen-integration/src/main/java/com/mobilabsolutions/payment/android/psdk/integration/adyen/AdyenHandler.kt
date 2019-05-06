package com.mobilabsolutions.payment.android.psdk.integration.adyen

import android.app.Application
import android.content.Context
import android.util.Base64
import com.adyen.checkout.core.card.Card
import com.adyen.checkout.core.card.CardType
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
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApiV2
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasExtra
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasUpdateRequest
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.SepaConfig
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.CreditCardRegistrationRequest
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
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
class AdyenHandler @Inject constructor(private val mobilabApiV2: MobilabApiV2, val context: Context) {

    fun registerCreditCard(
        creditCardRegistrationRequest: CreditCardRegistrationRequest,
        additionalData: AdditionalRegistrationData
    ): Single<String> {
        val singleResult = Single.create<String> {

            val billingData = creditCardRegistrationRequest.billingData
            val creditCardData = creditCardRegistrationRequest.creditCardData

            val paymentSessionString = additionalData.extraData["paymentSession"]
                    ?: throw RuntimeException("Missing payment session")
            val decodedPayemntSessionDebug = String(Base64.decode(paymentSessionString, Base64.DEFAULT))
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

            val cardCheckoutMethodFactory = CardCheckoutMethodFactory(context as Application)
            val cardCheckoutMethod = cardCheckoutMethodFactory.initCheckoutMethods(paymentSession).call()
            val resolvedPaymentMethod = cardCheckoutMethod[0].paymentMethod as PaymentMethodImpl
            val publicKey = paymentSession.publicKey!!
            val card = Card.Builder()
                    .setNumber(creditCardData.number)
                    .setExpiryDate(creditCardData.expiryDate.monthValue, creditCardData.expiryDate.year)
                    .setSecurityCode(creditCardData.cvv)
                    .build()
            val encryptedCard = Cards.ENCRYPTOR.encryptFields(card, paymentSession.generationTime, publicKey).call()
            val visaCardDetails = CardDetails.Builder()
                    .setHolderName(billingData.firstAndLastName)
                    .setEncryptedCardNumber(encryptedCard.encryptedNumber)
                    .setEncryptedExpiryMonth(encryptedCard.encryptedExpiryMonth)
                    .setEncryptedExpiryYear(encryptedCard.encryptedExpiryYear)
                    .setEncryptedSecurityCode(encryptedCard.encryptedSecurityCode)
                    .build()

            val paymentInitiation = PaymentInitiation.Builder(
                    paymentSession.paymentData, resolvedPaymentMethod.paymentMethodData)
                    .setPaymentMethodDetails(visaCardDetails)
                    .build()

            val paymentInitiationCallable = CheckoutApi
                    .getInstance(context.applicationContext as Application)
                    .initiatePayment(paymentSession, paymentInitiation)
            val paymentInitiationResult = Single.fromCallable {
                paymentInitiationCallable.call()
            }.subscribeOn(Schedulers.io()).blockingGet()

            mobilabApiV2.updateAlias(creditCardRegistrationRequest.aliasId, AliasUpdateRequest(
                    extra = AliasExtra(
                            paymentMethod = PaymentMethodType.CC.name,
                            payload = paymentInitiationResult.completeFields!!.payload

                    )
            )).subscribeOn(Schedulers.io()).blockingAwait()
            Timber.d("Payment init complete")
            if (paymentInitiationResult.type == PaymentInitiationResponse.Type.COMPLETE) {
                it.onSuccess(creditCardRegistrationRequest.aliasId)
            }
        }.subscribeOn(AndroidSchedulers.mainThread())
        return singleResult
    }

    fun registerSepa(
        aliasId: String,
        sepaData: SepaData,
        billingData: BillingData
    ): Single<String> {

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
                        extra = AliasExtra(sepaConfig = sepaConfig, paymentMethod = PaymentMethodType.SEPA.name)
                )
        ).andThen(Single.just(aliasId))
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