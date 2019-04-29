package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration

import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.CreditCardRegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodDefinition
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */

class BsOldIntegration(paymentSdkComponent: PaymentSdkComponent, val url: String = BuildConfig.oldBsApiUrl) : Integration {
    override val identifier = "BsOld"

    companion object : IntegrationCompanion {
        var integration: BsOldIntegration? = null

        override fun create(): IntegrationInitialization {
            return object : IntegrationInitialization {
                override fun initializedOrNull(): Integration? {
                    return integration
                }

                override fun initialize(paymentSdkComponent: PaymentSdkComponent, url: String): Integration {
                    if (integration == null) {
                        if (url.isEmpty()) {
                            integration = BsOldIntegration(paymentSdkComponent)
                        } else {
                            integration = BsOldIntegration(paymentSdkComponent, url)
                        }
                    }
                    return integration as Integration
                }
            }
        }
    }

    @Inject
    lateinit var oldBsPayoneHandler: OldBsPayoneHandler

    init {
        initialize(paymentSdkComponent)
    }

    private fun initialize(appDaggerGraph: PaymentSdkComponent) {
        val graph = DaggerBsOldIntegrationComponent.builder()
                .bsOldModule(BsOldModule(url))
                .coreComponent(appDaggerGraph)
                .build()
        graph.inject(this)
    }

    override fun getPreparationData(method : PaymentMethodType): Single<Map<String, String>> {
        return Single.just(emptyMap())
    }

    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {
        val standardizedData = registrationRequest.standardizedData

        return when (standardizedData) {
            is CreditCardRegistrationRequest -> {
                val additionalData = registrationRequest.additionalData
                val bsOldRegistrationRequest = BsOldRegistrationRequest.fromMapWitchCCData(additionalData.extraData, standardizedData.creditCardData)
                return oldBsPayoneHandler.registerCreditCard(standardizedData.aliasId, bsOldRegistrationRequest)
            }
            else -> throw BsOldIntegrationException("Invalid standardized data type")
        }
    }

    override fun handlePaymentMethodEntryRequest(activity: AppCompatActivity, paymentMethodDefinition: PaymentMethodDefinition): Single<Map<String, String>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getSupportedPaymentMethodDefinitions(): List<PaymentMethodDefinition> {
        return listOf(
                PaymentMethodDefinition(
                        methodId = "BsP-CC-1234",
                        pspIdentifier = identifier,
                        paymentMethodType = PaymentMethodType.CC
                )
        )
    }
}
