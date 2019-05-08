package com.mobilabsolutions.payment.android.psdk.integration.adyen

import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.integration.adyen.uicomponents.UiComponentHandler
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
/* ktlint-disable no-wildcard-imports */
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.*
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodDefinition
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
                        registrationRequest.standardizedData.aliasId,
                        standardizedData.sepaData,
                        standardizedData.billingData
                )
            }
            else -> {
                throw RuntimeException("Unsupported payment method")
            }
        }
    }

    override fun getSupportedPaymentMethodDefinitions(): List<PaymentMethodDefinition> {
        return listOf(creditCardUIDefinition, sepaUIDefinition)
    }

    override fun handlePaymentMethodEntryRequest(activity: AppCompatActivity, paymentMethodDefinition: PaymentMethodDefinition): Single<Map<String, String>> {
        return when (paymentMethodDefinition.paymentMethodType) {
            PaymentMethodType.CC -> uiComponentHandler.handleCreditCardDataEntryRequest(activity)
            PaymentMethodType.SEPA -> uiComponentHandler.handleSepaDataEntryRequest(activity)
            PaymentMethodType.PAYPAL -> throw RuntimeException("PayPal is not supported in BsPayone integration")
        }
    }
}
