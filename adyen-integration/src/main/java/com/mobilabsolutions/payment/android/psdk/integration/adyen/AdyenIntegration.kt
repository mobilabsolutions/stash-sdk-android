package com.mobilabsolutions.payment.android.psdk.integration.adyen

import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.integration.adyen.uicomponents.UiComponentHandler
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
/* ktlint-disable no-wildcard-imports */
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.*
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodDefinition
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.UiRequestHandler
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class AdyenIntegration @Inject constructor(paymentSdkComponent: PaymentSdkComponent) : Integration {
    override val identifier = "ADYEN"

    @Inject
    lateinit var adyenHandler: AdyenHandler

    @Inject
    lateinit var uiComponentHandler: UiComponentHandler

    companion object : IntegrationCompanion {
        var integration: AdyenIntegration? = null

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

    val adyenIntegrationComponent: AdyenIntegrationComponent = DaggerAdyenIntegrationComponent.builder()
            .paymentSdkComponent(paymentSdkComponent)
            .build()

    init {

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
        return adyenHandler.getPreparationData(method)
    }

    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {

        val standardizedData = registrationRequest.standardizedData
        val additionalData = registrationRequest.additionalData
        return when (standardizedData) {
            is CreditCardRegistrationRequest -> {
                adyenHandler.registerCreditCard(standardizedData, additionalData)
            }
            is SepaRegistrationRequest -> {
                adyenHandler.registerSepa(
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
        resultObservable: Observable<UiRequestHandler.DataEntryResult>
    ): Observable<AdditionalRegistrationData> {
        return when (paymentMethodType) {
            PaymentMethodType.CC -> uiComponentHandler.handleCreditCardDataEntryRequest(activity, resultObservable)
            PaymentMethodType.SEPA -> uiComponentHandler.handleSepaDataEntryRequest(activity, resultObservable)
            PaymentMethodType.PAYPAL -> throw RuntimeException("PayPal is not supported in BsPayone integration")
        }
    }
}
