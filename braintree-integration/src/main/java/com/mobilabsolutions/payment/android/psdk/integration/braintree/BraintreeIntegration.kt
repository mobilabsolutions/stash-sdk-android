/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk.integration.braintree

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.IdempotencyKey
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApi
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasExtra
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasUpdateRequest
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.PayPalConfig
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.UiRequestHandler
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 *
 * Braintree integration module.
 *
 * This integration supports PayPal as a payment method. Since Braintree SDK offers only UI based
 * method registration, this integration does the same.
 *
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BraintreeIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {
    override val identifier = "BRAINTREE"

    @Inject
    lateinit var braintreeHandler: BraintreeHandler

    @Inject
    lateinit var mobilabApi: MobilabApi

    @Inject
    lateinit var applicationContext: Context

    companion object : IntegrationCompanion {
        const val CLIENT_TOKEN = "clientToken"
        const val NONCE = "NONCE"
        const val DEVICE_FINGERPRINT = "DEVICE_FINGERPRINT"
        var integration: BraintreeIntegration? = null

        override val supportedPaymentMethodTypes: Set<PaymentMethodType> = setOf(PaymentMethodType.PAYPAL)

        override fun create(enabledPaymentMethodTypeSet: Set<PaymentMethodType>): IntegrationInitialization {
            return object : IntegrationInitialization {

                override val enabledPaymentMethodTypes = enabledPaymentMethodTypeSet

                override fun initializedOrNull(): Integration? {
                    return integration
                }

                override fun initialize(paymentSdkComponent: PaymentSdkComponent, url: String): Integration {
                    if (integration == null) {
                        integration = BraintreeIntegration(paymentSdkComponent)
                    }
                    return integration as Integration
                }
            }
        }
    }

    internal val braintreeIntegrationComponent: BraintreeIntegrationComponent

    init {
        braintreeIntegrationComponent = DaggerBraintreeIntegrationComponent.builder()
            .paymentSdkComponent(paymentSdkComponent)
            .build()
        braintreeIntegrationComponent.inject(this)
    }

    override fun getPreparationData(method: PaymentMethodType): Single<Map<String, String>> {
        return Single.just(emptyMap())
    }

    override fun handleRegistrationRequest(
        registrationRequest: RegistrationRequest,
        idempotencyKey: IdempotencyKey
    ): Single<String> {

        if (idempotencyKey.isUserSupplied) {
            Timber.w(applicationContext.getString(R.string.idempotency_message))
        }

        return mobilabApi.updateAlias(
            registrationRequest.standardizedData.aliasId,
            AliasUpdateRequest(
                extra = AliasExtra(
                    payPalConfig = PayPalConfig(
                        nonce = registrationRequest.additionalData.extraData[NONCE] ?: error("Missing nonce"),
                        deviceData = registrationRequest.additionalData.extraData[DEVICE_FINGERPRINT] ?: error("Missing device fingerprint")
                    ),
                    paymentMethod = "PAY_PAL",
                    personalData = BillingData(email = registrationRequest.additionalData.extraData[BillingData.ADDITIONAL_DATA_EMAIL])
                )
            )
        ).subscribeOn(Schedulers.io()).andThen(
            Single.just(registrationRequest.standardizedData.aliasId)
        )
    }

    override fun handlePaymentMethodEntryRequest(
        activity: AppCompatActivity,
        paymentMethodType: PaymentMethodType,
        additionalRegistrationData: AdditionalRegistrationData,
        resultObservable: Observable<UiRequestHandler.DataEntryResult>
    ): Observable<AdditionalRegistrationData> {

        return braintreeHandler.tokenizePaymentMethods(activity, additionalRegistrationData).flatMapObservable {
            Observable.just(
                AdditionalRegistrationData(
                    mapOf(
                        BillingData.ADDITIONAL_DATA_EMAIL to it.first,
                        NONCE to it.second,
                        DEVICE_FINGERPRINT to it.third
                    )
                )
            )
        }
    }
}
