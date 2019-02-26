package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration

import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.*
import com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.oldbspayone.OldBsPayoneHandler
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */



class BsOldIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {

    val OLD_BS_PAYONE_URL: String = BuildConfig.oldBsApiUrl


    companion object {
        fun create() : IntegrationInitialization {
            return object : IntegrationInitialization {
                override fun initialize(paymentSdkComponent: PaymentSdkComponent?): Integration {
                    return BsOldIntegration(paymentSdkComponent!!)
                }

            }
        }
    }

    @Inject
    lateinit var oldBsPayoneHandler : OldBsPayoneHandler

    init {
        initialize(paymentSdkComponent)
    }

    override fun initialize(appDaggerGraph : PaymentSdkComponent) {
        val graph = DaggerBsOldIntegrationComponent.builder()
                .bsOldModule(BsOldModule(OLD_BS_PAYONE_URL))
                .coreComponent(appDaggerGraph)
                .build()
        graph.inject(this)
        println("Got handler: $oldBsPayoneHandler")
    }

    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}



