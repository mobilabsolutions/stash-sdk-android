/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.sample.appinitializers

import android.app.Application
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.PaymentSdkConfiguration
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreeIntegration
import com.mobilabsolutions.payment.android.psdk.integration.adyen.AdyenIntegration
import com.mobilabsolutions.payment.sample.BuildConfig
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class PaymentSdkInitializer @Inject constructor() : AppInitializer {
    override fun init(application: Application) {
        val paymentSdkConfiguration = PaymentSdkConfiguration(
                publishableKey = BuildConfig.newBsApiKey,
                endpoint = BuildConfig.mobilabBackendUrl,
                integrationList = listOf(
                    AdyenIntegration to PaymentMethodType.CC,
                    AdyenIntegration to PaymentMethodType.SEPA,
                    BraintreeIntegration to PaymentMethodType.PAYPAL
                ),
                testMode = true
        )
        PaymentSdk.initalize(
                application,
                paymentSdkConfiguration
        )
    }
}