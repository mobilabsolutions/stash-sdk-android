package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.uicomponents.UiComponentHandler
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.CreditCardRegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.SepaRegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodDefinition
import io.reactivex.Single
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

    companion object : IntegrationCompanion {
        var integration: BsPayoneIntegration? = null

        override fun create(): IntegrationInitialization {
            return object : IntegrationInitialization {
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

    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {
        val standardizedData = registrationRequest.standardizedData
        val additionalData = registrationRequest.additionalData
        return when (standardizedData) {
            is CreditCardRegistrationRequest -> {
                bsPayoneHandler.registerCreditCard(
                        registrationRequest.standardizedData.aliasId,
                        BsPayoneRegistrationRequest.fromMap(additionalData.extraData),
                        standardizedData.creditCardData
                )
            }
            is SepaRegistrationRequest -> {
                bsPayoneHandler.registerSepa(
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

    val creditCardUIDefinition = PaymentMethodDefinition(
            methodId = "BsP-CC-1234",
            pspIdentifier = identifier,
            paymentMethodType = PaymentMethodType.CC
    )

    val sepaUIDefinition = PaymentMethodDefinition(
            methodId = "BsP-SEPA-1234",
            pspIdentifier = identifier,
            paymentMethodType = PaymentMethodType.SEPA
    )

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
