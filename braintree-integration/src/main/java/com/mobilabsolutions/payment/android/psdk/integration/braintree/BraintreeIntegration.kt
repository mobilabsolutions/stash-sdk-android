package com.mobilabsolutions.payment.android.psdk.integration.braintree

import android.app.Activity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApiV2
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasUpdateRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.PayPalRegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodDefinition
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BraintreeIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {
    override val identifier = "BRAINTREE"

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

    internal val braintreeIntegrationComponent : BraintreeIntegrationComponent

    init {
        braintreeIntegrationComponent = DaggerBraintreeIntegrationComponent.builder()
                .paymentSdkComponent(paymentSdkComponent)
                .build()
        braintreeIntegrationComponent.inject(this)
    }

    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {
        val backendImplemented = false
        return if (backendImplemented) {
            braintreeHandler.tokenizePaymentMethods()
                    .flatMap {
                        mobilabApiV2.updateAlias(registrationRequest.standardizedData.aliasId,
                                AliasUpdateRequest(it)).blockingAwait()
                        Single.just(it)
                    }
        } else {
            braintreeHandler.tokenizePaymentMethods()
        }

    }

    override fun getSupportedPaymentMethodDefinitions(): List<PaymentMethodDefinition> {
        return listOf(PaymentMethodDefinition("PayPal", identifier, PaymentMethodType.PAYPAL))
    }

    override fun handlePaymentMethodEntryRequest(activity : Activity, registrationRequest: RegistrationRequest): Single<String> {
        return handleRegistrationRequest(registrationRequest)
    }
}



