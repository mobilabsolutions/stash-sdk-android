package com.mobilabsolutions.payment.android.psdk.integration.stripeintegration

import android.content.Context
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentManager
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.DeletionRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.PaymentRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.stripe.android.Stripe
import io.reactivex.Single

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
internal class StripeIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {
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

    override fun initialize(context: Context) {
        stripe = StripeInitializer.initialize(context)
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

    fun RegistrationManager.registerCreditCard(creditCardData: CreditCardData, stripeAdditionalRegistrationData: StripeAdditionalRegistrationData): Single<String> {
        return Single.just("Test")
    }


}



