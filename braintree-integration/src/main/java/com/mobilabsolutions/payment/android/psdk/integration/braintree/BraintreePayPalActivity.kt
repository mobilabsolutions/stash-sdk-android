package com.mobilabsolutions.payment.android.psdk.integration.braintree

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.braintreepayments.api.BraintreeFragment
import com.braintreepayments.api.DataCollector
import com.braintreepayments.api.PayPal
import com.braintreepayments.api.interfaces.BraintreeCancelListener
import com.braintreepayments.api.interfaces.BraintreeErrorListener
import com.braintreepayments.api.interfaces.ConfigurationListener
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener
import com.braintreepayments.api.models.Configuration
import com.braintreepayments.api.models.PayPalAccountNonce
import com.braintreepayments.api.models.PayPalRequest
import com.braintreepayments.api.models.PaymentMethodNonce
import com.mobilabsolutions.payment.android.psdk.exceptions.base.ConfigurationException
import com.mobilabsolutions.payment.android.psdk.exceptions.base.OtherException
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.ReplaySubject
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BraintreePayPalActivity : AppCompatActivity(), ConfigurationListener,
        PaymentMethodNonceCreatedListener, BraintreeErrorListener, BraintreeCancelListener {

    val deviceFingerprintSubject = ReplaySubject.create<String>()

    var pointOfNoReturnReached: Boolean = false

    override fun onCancel(requestCode: Int) {
        braintreeHandler.resultSubject.onError(RuntimeException("Braintree canceled"))
        this.finish()
    }

    override fun onConfigurationFetched(configuration: Configuration?) {
        Timber.d("Got configuration")
    }

    @Inject
    lateinit var braintreeHandler: BraintreeHandler

    override fun onPaymentMethodNonceCreated(paymentMethodNonce: PaymentMethodNonce?) {
        Timber.d("Payment nonce create")
        if (paymentMethodNonce is PayPalAccountNonce) {
            val disposable = deviceFingerprintSubject.firstOrError().subscribeBy(
                    onSuccess = {
                        braintreeHandler.resultSubject.onNext(
                                Triple(
                                        paymentMethodNonce.description
                                                ?: paymentMethodNonce.typeLabel ?: "",
                                        paymentMethodNonce.nonce
                                                ?: throw RuntimeException("Nonce was null in created method"),
                                        it
                                        )
                        )
                        braintreeHandler.resultSubject.onComplete()
                    },
                    onError = {
                        braintreeHandler.resultSubject.onError(it)
                        braintreeHandler.resultSubject.onComplete()
                    }
            )
        }

        this.finish()
    }

    override fun onError(error: Exception?) {
        val wrappedException = when (error) {
            is com.braintreepayments.api.exceptions.ConfigurationException -> ConfigurationException(originalException = error)
            else -> OtherException(originalException = error)
        }
        braintreeHandler.resultSubject.onError(wrappedException)
        this.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BraintreeIntegration.integration?.braintreeIntegrationComponent?.inject(this)

        val token = intent.getStringExtra(BraintreeIntegration.CLIENT_TOKEN)

        setContentView(R.layout.activity_braintree_paypal)

        val braintreeFragment = BraintreeFragment.newInstance(this, token)

        DataCollector.collectDeviceData(braintreeFragment) { t ->
            if (t != null) {
                deviceFingerprintSubject.onNext(t)
            } else {
                deviceFingerprintSubject.onError(OtherException("Couldn't get Braintree device data"))
            }
        }
        val payment = PayPalRequest()
        PayPal.requestBillingAgreement(braintreeFragment, payment)
        pointOfNoReturnReached = true
    }

    // We want to disable back press once the request has been sent out, because otherwise it will
    // go back to the main screen and then launch braintree activity
    override fun onBackPressed() {
        if (!pointOfNoReturnReached) {
            super.onBackPressed()
            braintreeHandler.resultSubject.onError(OtherException("User cancelled PayPal registration"))
        }
    }
}