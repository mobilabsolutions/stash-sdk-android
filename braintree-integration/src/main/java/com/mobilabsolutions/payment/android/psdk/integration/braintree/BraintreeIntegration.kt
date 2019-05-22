package com.mobilabsolutions.payment.android.psdk.integration.braintree

import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApiV2
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasExtra
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.AliasUpdateRequest
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.v2.PayPalConfig
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodDefinition
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BraintreeIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {
    override val identifier = "BRAINTREE"

    val DESCRIPTION = "DESCRIPTION"
    val NONCE = "NONCE"
    val DEVICE_FINGERPRINT = "DEVICE_FINGERPRINT"

    @Inject
    lateinit var braintreeHandler: BraintreeHandler

    @Inject
    lateinit var mobilabApiV2: MobilabApiV2

    companion object : IntegrationCompanion {
        val CLIENT_TOKEN = "clientToken"

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

    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {
        return mobilabApiV2.updateAlias(
                registrationRequest.standardizedData.aliasId,
                AliasUpdateRequest(
                        extra = AliasExtra(
                                payPalConfig = PayPalConfig(
                                        nonce = registrationRequest.additionalData.extraData[NONCE]!!,
                                        deviceData = registrationRequest.additionalData.extraData[DEVICE_FINGERPRINT]!!
                                ),
//                                paymentMethod = PaymentMethodType.PAYPAL.name
                                paymentMethod = "PAY_PAL"

                        )
                )
        ).subscribeOn(Schedulers.io()).andThen(
            Single.just(registrationRequest.standardizedData.aliasId)
        )
    }

    override fun handlePaymentMethodEntryRequest(activity: AppCompatActivity, paymentMethodType: PaymentMethodType, additionalRegistrationData: AdditionalRegistrationData): Single<Map<String, String>> {
        return braintreeHandler.tokenizePaymentMethods(activity, additionalRegistrationData).flatMap {
            Single.just(
                    mapOf(
                            DESCRIPTION to it.first,
                            NONCE to it.second,
                            DEVICE_FINGERPRINT to it.third

                    )
            )
        }
    }
}
