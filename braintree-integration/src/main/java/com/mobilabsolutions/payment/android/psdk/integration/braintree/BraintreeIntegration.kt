package com.mobilabsolutions.payment.android.psdk.integration.braintree

import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApiV2
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasUpdateRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodDefinition
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BraintreeIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {
    override val identifier = "BRAINTREE"

    val NONCE = "nonce"

    @Inject
    lateinit var braintreeHandler: BraintreeHandler

    @Inject
    lateinit var mobilabApiV2: MobilabApiV2

    companion object : IntegrationCompanion {
        var integration: BraintreeIntegration? = null

        override fun create(): IntegrationInitialization {
            return object : IntegrationInitialization {
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

    override fun getPreparationData(method : PaymentMethodType): Single<Map<String, String>> {
        return Single.just(emptyMap())
    }

    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {
        val backendImplemented = false
        return if (backendImplemented) {
            mobilabApiV2.updateAlias(registrationRequest.standardizedData.aliasId,
                    AliasUpdateRequest(registrationRequest.additionalData.extraData[NONCE])).blockingAwait()
            Single.just(registrationRequest.standardizedData.aliasId)
        } else {
            Single.just(registrationRequest.additionalData.extraData[NONCE])
        }
    }

    override fun getSupportedPaymentMethodDefinitions(): List<PaymentMethodDefinition> {
        return listOf(PaymentMethodDefinition("PayPal", identifier, PaymentMethodType.PAYPAL))
    }

    override fun handlePaymentMethodEntryRequest(activity: AppCompatActivity, paymentMethodDefinition: PaymentMethodDefinition): Single<Map<String, String>> {
        return braintreeHandler.tokenizePaymentMethods(activity).flatMap { Single.just(mapOf(NONCE to it)) }
    }
}
