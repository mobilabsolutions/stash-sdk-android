package com.mobilabsolutions.payment.sample.appinitializers

import android.app.Application
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreeIntegration
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import com.mobilabsolutions.payment.sample.BuildConfig
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class PaymentSdkInitializer @Inject constructor() : AppInitializer {
    override fun init(application: Application) {
        PaymentSdk.initalize(
                BuildConfig.newBsApiKey,
                BuildConfig.mobilabBackendUrl,
                application,
                setOf(BsPayoneIntegration, BraintreeIntegration),
                true
        )
    }
}