package com.mobilabsolutions.payment.android.psdk.integration.braintree

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.braintreepayments.api.BraintreeFragment
import com.braintreepayments.api.DataCollector
import com.braintreepayments.api.PayPal
import com.braintreepayments.api.interfaces.BraintreeCancelListener
import com.braintreepayments.api.interfaces.BraintreeErrorListener
import com.braintreepayments.api.interfaces.BraintreeResponseListener
import com.braintreepayments.api.interfaces.ConfigurationListener
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener
import com.braintreepayments.api.models.Configuration
import com.braintreepayments.api.models.PayPalAccountNonce
import com.braintreepayments.api.models.PayPalRequest
import com.braintreepayments.api.models.PaymentMethodNonce
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.exceptions.base.OtherException
import io.reactivex.Single
import io.reactivex.SingleEmitter
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
                                        paymentMethodNonce?.description
                                                ?: paymentMethodNonce?.typeLabel ?: "",
                                        paymentMethodNonce?.nonce
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
        Timber.d(error, "Error")
        braintreeHandler.resultSubject.onError(error
                ?: RuntimeException("Unknown error received from Braintree SDK"))
        this.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BraintreeIntegration.integration?.braintreeIntegrationComponent?.inject(this)

        val token = intent.getStringExtra("clientToken")

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
    }
}