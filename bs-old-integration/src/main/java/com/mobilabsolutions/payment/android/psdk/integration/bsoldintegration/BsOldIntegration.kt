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
class BsOldIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {
    override fun initialize(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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



