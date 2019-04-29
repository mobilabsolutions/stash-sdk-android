package com.mobilabsolutions.payment.android.psdk.integration.adyen

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.adyen.checkout.core.CheckoutException
import com.adyen.checkout.core.PaymentController
import com.adyen.checkout.core.PaymentSetupParameters
import com.adyen.checkout.core.handler.PaymentSetupParametersHandler
import com.adyen.checkout.core.internal.PaymentSetupParametersImpl
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.*
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodDefinition
import io.reactivex.Single
import java.lang.RuntimeException
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class AdyenIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {
    override val identifier = "Adyen"

    @Inject
    lateinit var context: Context

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
        val standardizedData = registrationRequest.standardizedData
        val additionalData = registrationRequest.additionalData
        TODO()
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
}
