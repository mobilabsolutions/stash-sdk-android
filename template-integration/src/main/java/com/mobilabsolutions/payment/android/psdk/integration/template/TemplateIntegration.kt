package com.mobilabsolutions.payment.android.psdk.integration.template

import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodDefinition
import io.reactivex.Single

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class TemplateIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {

    override val identifier = "Template"

    companion object : IntegrationCompanion {
        var integration: TemplateIntegration? = null

        override fun create(): IntegrationInitialization {
            return object : IntegrationInitialization {
                override fun initializedOrNull(): Integration? {
                    return integration
                }

                override fun initialize(paymentSdkComponent: PaymentSdkComponent, url: String): Integration {
                    if (integration == null) {
                        if (url.isEmpty()) {
                            integration = TemplateIntegration(paymentSdkComponent)
                        } else {
                            TODO("Handle specific url here if PSP supports specific urls")
                        }
                    }
                    return integration as Integration
                }
            }
        }
    }

    val templateIntegrationComponent: TemplateIntegrationComponent

    init {
        templateIntegrationComponent = DaggerTemplateIntegrationComponent.builder()
                .paymentSdkComponent(paymentSdkComponent)
                .build()
    }

    override fun getPreparationData(method: PaymentMethodType): Single<Map<String, String>> {
        return Single.just(emptyMap())
    }

    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {

        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getSupportedPaymentMethodDefinitions(): List<PaymentMethodDefinition> {
        return emptyList()
    }

    override fun handlePaymentMethodEntryRequest(activity: AppCompatActivity, paymentMethodDefinition: PaymentMethodDefinition, additionalRegistrationData: AdditionalRegistrationData): Single<Map<String, String>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
