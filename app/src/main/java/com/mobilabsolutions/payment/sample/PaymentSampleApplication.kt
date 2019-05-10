package com.mobilabsolutions.payment.sample

import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobilabsolutions.commonsv3_dagger.mvp.presenter.DaggerPresenterManager
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.PaymentSdkConfiguration
import com.mobilabsolutions.payment.android.psdk.integration.adyen.AdyenIntegration
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreeIntegration
import com.mobilabsolutions.payment.android.psdk.internal.CustomizationPreference
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PaymentSampleApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        val configuration = PaymentSdkConfiguration(
                publicKey = BuildConfig.newBsApiKey,
                endpoint = "https://payment-dev.mblb.net/api/",
                integrations = setOf(AdyenIntegration, BraintreeIntegration),
                testMode = true
        )
        PaymentSdk.initalize(this, configuration)
//        PaymentSdk.initalize(BuildConfig.newBsApiKey, "https://payment-dev.mblb.net/api/", this, setOf(BsPayoneIntegration, BraintreeIntegration), true)

        val textColor: Int = android.R.color.holo_orange_dark
        val backgroundColor: Int = R.color.coral
        val buttonColor: Int = android.R.color.holo_purple
        val buttonTextColor: Int = android.R.color.holo_blue_bright
        val cellBackgroundColor: Int = R.color.lochmara
        val mediumEmphasisColor: Int = android.R.color.holo_green_light

        val customizationPreference = CustomizationPreference(
                textColor,
                backgroundColor,
                buttonColor,
                buttonTextColor,
                cellBackgroundColor,
                mediumEmphasisColor
        )
        PaymentSdk.getUiCustomizationManager().setCustomizationPreferences(customizationPreference)

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            if (Timber.forest().filter { it is Timber.DebugTree }.isEmpty()) {
                Timber.plant(Timber.DebugTree())
            }
        }

        AndroidThreeTen.init(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component = DaggerAppComponent.builder().application(this).build().also { it.inject(this) }
        DaggerPresenterManager.setInjector(component)
        return component
    }
}