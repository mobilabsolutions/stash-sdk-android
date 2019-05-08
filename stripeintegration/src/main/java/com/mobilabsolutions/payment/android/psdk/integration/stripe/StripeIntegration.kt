package com.mobilabsolutions.payment.android.psdk.integration.stripe

import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodDefinition
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

    val stripeIntegrationComponent: StripeIntegrationComponent

    init {
        stripeIntegrationComponent = DaggerStripeIntegrationComponent.builder()
                .paymentSdkComponent(paymentSdkComponent)
                .build()
    }

    lateinit var stripe: Stripe

    override fun getPreparationData(method: PaymentMethodType): Single<Map<String, String>> {
        return Single.just(emptyMap())
    }

    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {

        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getSupportedPaymentMethodDefinitions(): List<PaymentMethodDefinition> {
        return emptyList()
    }

    override fun handlePaymentMethodEntryRequest(activity: AppCompatActivity, paymentMethodDefinition: PaymentMethodDefinition): Single<Map<String, String>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
