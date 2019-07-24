/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.braintree

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.braintreepayments.api.BraintreeFragment
import com.braintreepayments.api.Card
import com.braintreepayments.api.interfaces.BraintreeCancelListener
import com.braintreepayments.api.interfaces.BraintreeErrorListener
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener
import com.braintreepayments.api.models.CardBuilder
import com.braintreepayments.api.models.PaymentMethodNonce
import com.mobilabsolutions.stash.braintree.internal.uicomponents.BraintreePayPalActivity
import com.mobilabsolutions.stash.core.exceptions.base.ConfigurationException
import com.mobilabsolutions.stash.core.exceptions.base.OtherException
import com.mobilabsolutions.stash.core.internal.IntegrationScope
import com.mobilabsolutions.stash.core.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.stash.core.internal.psphandler.CreditCardRegistrationRequest
import com.mobilabsolutions.stash.core.internal.uicomponents.UiRequestHandler
import com.mobilabsolutions.stash.core.model.BillingData
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
class BraintreeHandler @Inject constructor() {

    private val processing = AtomicBoolean(false)
    internal var resultSubject = PublishSubject.create<Triple<String, String, String>>()

    fun tokenizePaymentMethods(
        activity: AppCompatActivity,
        additionalRegistrationData: AdditionalRegistrationData
    ): Single<Triple<String, String, String>> {
        return if (processing.compareAndSet(false, true)) {
            resultSubject = PublishSubject.create()
            val payPalActivityIntent = Intent(activity, BraintreePayPalActivity::class.java)
            payPalActivityIntent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
            payPalActivityIntent.putExtra(BraintreeIntegration.CLIENT_TOKEN, additionalRegistrationData.extraData[BraintreeIntegration.CLIENT_TOKEN])
            activity.startActivity(payPalActivityIntent)
            resultSubject
                .doOnEach {
                    Timber.d("Event from PayPal activity $it")
                }
                .doFinally {
                    Timber.d("Finalizing")
                    processing.set(false)
                }
                .firstOrError()
        } else {
            Single.error(RuntimeException("Braintree PayPal activity already shown!"))
        }
    }

    fun registerCreditCard(
        standardizedData: CreditCardRegistrationRequest,
        additionalData: AdditionalRegistrationData
    ): Single<String> {
        return Single.create {
            val braintreeFragment = BraintreeFragment.newInstance(AppCompatActivity(), additionalData.extraData[BraintreeIntegration.CLIENT_TOKEN])
            braintreeFragment.addListener(
                object : PaymentMethodNonceCreatedListener {
                    override fun onPaymentMethodNonceCreated(paymentMethodNonce: PaymentMethodNonce) {
                        resultSubject.onNext(
                            Triple(
                                paymentMethodNonce.description
                                    ?: paymentMethodNonce.typeLabel ?: "",
                                paymentMethodNonce.nonce
                                    ?: throw RuntimeException("Nonce was null in created method"),
                                ""
                            )
                        )
                    }
                }
            )
            braintreeFragment.addListener(
                object : BraintreeCancelListener {
                    override fun onCancel(requestCode: Int) {
                        resultSubject.onError(UiRequestHandler.UserCancelled())
                    }
                }
            )
            braintreeFragment.addListener(
                object : BraintreeErrorListener {
                    override fun onError(error: Exception) {
                        val wrappedException = when (error) {
                            is com.braintreepayments.api.exceptions.ConfigurationException -> ConfigurationException(originalException = error)
                            else -> OtherException(originalException = error)
                        }
                        resultSubject.onError(wrappedException)
                    }
                }
            )
            val cardBuilder = CardBuilder()
                .cardNumber(standardizedData.creditCardData.number)
                .expirationDate("${standardizedData.creditCardData.expiryMonth}/${standardizedData.creditCardData.expiryYear}")
                .cvv(standardizedData.creditCardData.cvv)
                .firstName(additionalData.extraData[BillingData.ADDITIONAL_DATA_FIRST_NAME])
                .lastName(additionalData.extraData[BillingData.ADDITIONAL_DATA_LAST_NAME])
                .countryCode("DE")

            Card.tokenize(braintreeFragment, cardBuilder)
        }
    }
}