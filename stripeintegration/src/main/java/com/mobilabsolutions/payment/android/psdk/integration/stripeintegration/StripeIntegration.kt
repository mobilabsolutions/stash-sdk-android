package com.mobilabsolutions.payment.android.psdk.integration.stripeintegration

import android.content.Context
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentManager
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.*
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.stripe.android.Stripe
import io.reactivex.Single

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class StripeIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {
    override val identifier = "Stripe"


    companion object {
        fun activate() {

        }
    }

    val stripeIntegrationComponent : StripeIntegrationComponent

    init {
        stripeIntegrationComponent = DaggerStripeIntegrationComponent.builder()
                .paymentSdkComponent(paymentSdkComponent)
                .build()
    }

    lateinit var stripe: Stripe


    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}



