package com.mobilabsolutions.payment.sample.appinitializers

import android.app.Application
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.PaymentSdkConfiguration
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreeIntegration
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import com.mobilabsolutions.payment.sample.BuildConfig
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class PaymentSdkInitializer @Inject constructor() : AppInitializer {
    override fun init(application: Application) {
        val paymentSdkConfiguration = PaymentSdkConfiguration(
                publicKey = BuildConfig.newBsApiKey,
                endpoint = BuildConfig.mobilabBackendUrl,
                integrations = setOf(BsPayoneIntegration, BraintreeIntegration),
                testMode = true
        )
        PaymentSdk.initalize(
                application,
                paymentSdkConfiguration
        )
    }
}