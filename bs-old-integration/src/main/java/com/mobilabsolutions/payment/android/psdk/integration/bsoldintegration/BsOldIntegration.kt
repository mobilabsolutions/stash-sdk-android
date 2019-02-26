package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration

import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.oldbspayone.OldBsPayoneHandler
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */



class BsOldIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {
    override val identifier = "BsOld"

    val OLD_BS_PAYONE_URL: String = BuildConfig.oldBsApiUrl


    companion object {
        var integration : BsOldIntegration? = null

        fun create() : IntegrationInitialization {
            return object : IntegrationInitialization {
                override fun initializedOrNull(): Integration? {
                    return integration
                }

                override fun initialize(paymentSdkComponent: PaymentSdkComponent): Integration {
                    if (integration == null) {
                        integration = BsOldIntegration(paymentSdkComponent)
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
                .bsOldModule(BsOldModule(OLD_BS_PAYONE_URL))
                .coreComponent(appDaggerGraph)
                .build()
        graph.inject(this)
        println("Got handler: $oldBsPayoneHandler")
    }

    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {

        val additionaLData = registrationRequest.getAdditionalData()
        val standardizedData = registrationRequest.getStandardizedData()
//        oldBsPayoneHandler.registerCreditCard()
        return Single.just("TODO")
    }




}



