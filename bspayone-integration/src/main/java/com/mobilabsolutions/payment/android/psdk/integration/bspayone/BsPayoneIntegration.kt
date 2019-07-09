/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.uicomponents.UiComponentHandler
import com.mobilabsolutions.payment.android.psdk.internal.IdempotencyKey
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.CreditCardRegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.SepaRegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.UiRequestHandler
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BsPayoneIntegration private constructor(
    paymentSdkComponent: PaymentSdkComponent,
    val url: String = BuildConfig.newBsApiUrl
) : Integration {
    override val identifier = "BS_PAYONE"

    @Inject
    lateinit var bsPayoneHandler: BsPayoneHandler

    @Inject
    lateinit var uiComponentHandler: UiComponentHandler

    @Inject
    lateinit var applicationContext: Context

    companion object : IntegrationCompanion {

        var integration: BsPayoneIntegration? = null

        override val supportedPaymentMethodTypes: Set<PaymentMethodType> = setOf(PaymentMethodType.CC, PaymentMethodType.SEPA)

        override fun create(enabledPaymentMethodTypeSet: Set<PaymentMethodType>): IntegrationInitialization {
            return object : IntegrationInitialization {
                override val enabledPaymentMethodTypes = enabledPaymentMethodTypeSet

                override fun initializedOrNull(): Integration? {
                    return integration
                }

                override fun initialize(paymentSdkComponent: PaymentSdkComponent, url: String): Integration {
                    if (integration == null) {
                        if (url.isEmpty()) {
                            integration = BsPayoneIntegration(paymentSdkComponent)
                        } else {
                            integration = BsPayoneIntegration(paymentSdkComponent, url)
                        }
                    }
                    return integration as Integration
                }
            }
        }
    }

    val bsPayoneIntegrationComponent: BsPayoneIntegrationComponent

    init {
        bsPayoneIntegrationComponent = DaggerBsPayoneIntegrationComponent.builder()
            .paymentSdkComponent(paymentSdkComponent)
            .bsPayoneModule(BsPayoneModule(url))
            .build()

        bsPayoneIntegrationComponent.inject(this)
    }

    override fun getPreparationData(method: PaymentMethodType): Single<Map<String, String>> {
        return Single.just(emptyMap())
    }

    override fun handleRegistrationRequest(
        registrationRequest: RegistrationRequest,
        idempotencyKey: IdempotencyKey
    ): Single<String> {
        val standardizedData = registrationRequest.standardizedData
        val additionalData = registrationRequest.additionalData
        return when (standardizedData) {
            is CreditCardRegistrationRequest -> {
                if (idempotencyKey.isUserSupplied) {
                    Timber.w(applicationContext.getString(R.string.idempotency_message))
                }
                bsPayoneHandler.registerCreditCard(
                    standardizedData,
                    BsPayoneRegistrationRequest.fromMap(additionalData.extraData)
                )
            }
            is SepaRegistrationRequest -> {
                bsPayoneHandler.registerSepa(
                    standardizedData
                )
            }
            else -> {
                throw RuntimeException("Unsupported payment method")
            }
        }
    }

    override fun handlePaymentMethodEntryRequest(
        activity: AppCompatActivity,
        paymentMethodType: PaymentMethodType,
        additionalRegistrationData: AdditionalRegistrationData,
        resultObservable: Observable<UiRequestHandler.DataEntryResult>,
        idempotencyKey: IdempotencyKey
    ): Observable<AdditionalRegistrationData> {
        return when (paymentMethodType) {
            PaymentMethodType.CC -> {
                if (idempotencyKey.isUserSupplied) {
                    Timber.w(applicationContext.getString(R.string.idempotency_message))
                }
                uiComponentHandler.handleCreditCardDataEntryRequest(activity, resultObservable)
            }
            PaymentMethodType.SEPA -> uiComponentHandler.handleSepaDataEntryRequest(activity, resultObservable)
            PaymentMethodType.PAYPAL -> throw RuntimeException("PayPal is not supported in BsPayone integration")
        }
    }
}
