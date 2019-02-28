package com.mobilabsolutions.payment.android.psdk.integration.stripeintegration

import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import io.reactivex.Single

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BsPayoneIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {
    override val identifier = "Stripe"


    companion object {
        fun activate() {

        }
    }

    val stripeIntegrationComponent : BsPayoneIntegrationComponent

    init {
        stripeIntegrationComponent = DaggerBsPayoneIntegrationComponent.builder()
                .paymentSdkComponent(paymentSdkComponent)
                .build()
    }



    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}



