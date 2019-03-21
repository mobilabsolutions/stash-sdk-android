package com.mobilabsolutions.payment.android.psdk.integration.braintree

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.braintreepayments.api.BraintreeFragment
import com.braintreepayments.api.PayPal
import com.braintreepayments.api.interfaces.BraintreeErrorListener
import com.braintreepayments.api.interfaces.BraintreeListener
import com.braintreepayments.api.interfaces.ConfigurationListener
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener
import com.braintreepayments.api.models.Configuration
import com.braintreepayments.api.models.PayPalRequest
import com.braintreepayments.api.models.PaymentMethodNonce
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.R
import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentSdk
import timber.log.Timber
import java.lang.Exception
import java.lang.RuntimeException
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BraintreePayPalActivity : AppCompatActivity(), ConfigurationListener,
        PaymentMethodNonceCreatedListener, BraintreeErrorListener {
    override fun onConfigurationFetched(configuration: Configuration?) {
        Timber.d("Got configuration")
    }

    @Inject
    lateinit var braintreeHandler: BraintreeHandler

    override fun onPaymentMethodNonceCreated(paymentMethodNonce: PaymentMethodNonce?) {
        Timber.d("Payment nonce create")
        braintreeHandler.resultSubject.onNext(paymentMethodNonce?.nonce ?: throw RuntimeException("Nonce was null in created method"))
        braintreeHandler.resultSubject.onComplete()
        this.finish()
    }

    override fun onError(error: Exception?) {
        Timber.d(error, "Got configuration")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BraintreeIntegration.integration?.braintreeIntegrationComponent?.inject(this)
        setContentView(R.layout.activity_braintree_paypal)
        val braintreeFragment = BraintreeFragment.newInstance(this, BuildConfig.braintreeSanboxToken)
        val payment = PayPalRequest()
        PayPal.requestBillingAgreement(braintreeFragment, payment)
    }
}