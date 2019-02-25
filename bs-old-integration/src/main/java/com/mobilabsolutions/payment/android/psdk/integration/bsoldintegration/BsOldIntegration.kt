package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration

import android.content.Context
import com.mobilabsolutions.payment.android.psdk.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.*
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.oldbspayone.OldBsPayoneHandler
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */



class BsOldIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {


    companion object {
        fun initialization() : IntegrationInitialization {
            return object : IntegrationInitialization {
                override fun initialize(paymentSdkComponent: PaymentSdkComponent?): Integration {
                    return BsOldIntegration(paymentSdkComponent!!)
                }

            }
        }
    }

    @Inject
    lateinit var oldBsPayoneHandler : OldBsPayoneHandler

    override fun initialize(context: Context, appDaggerGraph : PaymentSdkComponent) {
        val graph = DaggerBsOldIntegrationComponent.builder()
                .coreComponent(appDaggerGraph)
                .build()
        graph.inject(this)
        println("$oldBsPayoneHandler")
    }

    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handlePaymentRequest(paymentRequest: PaymentRequest): Single<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleDeletionRequest(deletionRequest: DeletionRequest): Single<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}



