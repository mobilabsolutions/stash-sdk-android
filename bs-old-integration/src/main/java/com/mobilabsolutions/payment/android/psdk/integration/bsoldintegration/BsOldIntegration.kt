package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration

import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.*
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */


class BsOldIntegration(paymentSdkComponent: PaymentSdkComponent, val url : String = BuildConfig.oldBsApiUrl ) : Integration {
    override val identifier = "BsOld"

    companion object : IntegrationCompanion {
        var integration : BsOldIntegration? = null

        override fun create() : IntegrationInitialization {
            return object : IntegrationInitialization {
                override fun initializedOrNull(): Integration? {
                    return integration
                }

                override fun initialize(paymentSdkComponent: PaymentSdkComponent, url : String): Integration {
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
    lateinit var oldBsPayoneHandler : OldBsPayoneHandler

    init {
        initialize(paymentSdkComponent)
    }

    private fun initialize(appDaggerGraph : PaymentSdkComponent) {
        val graph = DaggerBsOldIntegrationComponent.builder()
                .bsOldModule(BsOldModule(url))
                .coreComponent(appDaggerGraph)
                .build()
        graph.inject(this)
    }

    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {
        val standardizedData = registrationRequest.standardizedData

        return when (standardizedData) {
            is CreditCardRegistrationRequest -> {
                val additionalData = registrationRequest.additionalData
                val bsOldRegistrationRequest = BsOldRegistrationRequest.fromMapWitchCCData(additionalData.extraData, standardizedData.creditCardData)
                return oldBsPayoneHandler.registerCreditCard(standardizedData.aliasId, bsOldRegistrationRequest)
            }
            is SepaRegistrationRequest -> {
                return Single.just("123")
            }
            else -> throw BsOldIntegrationException("Invalid standardized data type")
        }

    }




}



