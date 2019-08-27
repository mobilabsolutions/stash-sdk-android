/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.appinitializers

import android.app.Application
import com.mobilabsolutions.stash.adyen.AdyenIntegration
import com.mobilabsolutions.stash.braintree.BraintreeIntegration
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.Stash
import com.mobilabsolutions.stash.core.StashConfiguration
import com.mobilabsolutions.stash.core.StashUiConfiguration
import com.mobilabsolutions.stash.sample.BuildConfig
import com.mobilabsolutions.stash.sample.R
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class StashInitializer @Inject constructor() : AppInitializer {
    override fun init(application: Application) {
        val stashConfiguration = StashConfiguration(
            publishableKey = BuildConfig.testPublishableKey,
            endpoint = BuildConfig.mobilabBackendUrl,
            integrationList = listOf(
                AdyenIntegration to PaymentMethodType.CC,
                AdyenIntegration to PaymentMethodType.SEPA,
                BraintreeIntegration to PaymentMethodType.PAYPAL
            ),
            testMode = true,
            stashUiConfiguration = StashUiConfiguration.Builder()
                .setSnackBarBackground(R.color.carnation).build()

        )
        Stash.initialize(
            application,
            stashConfiguration
        )
    }
}