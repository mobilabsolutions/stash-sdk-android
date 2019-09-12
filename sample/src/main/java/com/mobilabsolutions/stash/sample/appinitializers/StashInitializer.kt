/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.appinitializers

import android.app.Application
import com.mobilabsolutions.stash.adyen.AdyenIntegration
import com.mobilabsolutions.stash.braintree.BraintreeIntegration
import com.mobilabsolutions.stash.bspayone.BsPayoneIntegration
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.Stash
import com.mobilabsolutions.stash.core.StashConfiguration
import com.mobilabsolutions.stash.core.StashUiConfiguration
import com.mobilabsolutions.stash.core.internal.psphandler.IntegrationCompanion
import com.mobilabsolutions.stash.sample.BuildConfig
import com.mobilabsolutions.stash.sample.R
import com.mobilabsolutions.stash.sample.data.SamplePreference
import com.mobilabsolutions.stash.sample.data.SamplePreference.Psp.ADAYEN
import com.mobilabsolutions.stash.sample.data.SamplePreference.Psp.BRAINTREE
import com.mobilabsolutions.stash.sample.data.SamplePreference.Psp.BS_PAYONE
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class StashInitializer @Inject constructor(
    private val samplePreference: SamplePreference
) : AppInitializer {
    override fun init(application: Application) {
        val ccPsp: IntegrationCompanion = when (samplePreference.creditCardPreference) {
            ADAYEN -> AdyenIntegration
            BRAINTREE -> BraintreeIntegration
            BS_PAYONE -> BsPayoneIntegration
        }

        val sepaPsp: IntegrationCompanion = when (samplePreference.sepaPreference) {
            ADAYEN -> AdyenIntegration
            BRAINTREE -> BraintreeIntegration
            BS_PAYONE -> BsPayoneIntegration
        }
        val stashConfiguration = StashConfiguration(
            publishableKey = BuildConfig.testPublishableKey,
            endpoint = BuildConfig.mobilabBackendUrl,
            integrationList = listOf(
                ccPsp to PaymentMethodType.CC,
                sepaPsp to PaymentMethodType.SEPA,
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