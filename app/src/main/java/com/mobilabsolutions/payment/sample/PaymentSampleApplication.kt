package com.mobilabsolutions.payment.sample

import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobilabsolutions.commonsv3_dagger.mvp.presenter.DaggerPresenterManager
import com.mobilabsolutions.payment.android.psdk.PaymentUIConfiguration
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.PaymentSdkConfiguration
import com.mobilabsolutions.payment.android.psdk.integration.adyen.AdyenIntegration
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreeIntegration
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
                integrationList = listOf(
                    AdyenIntegration to PaymentMethodType.CC,
                    AdyenIntegration to PaymentMethodType.SEPA,
                    BraintreeIntegration to PaymentMethodType.PAYPAL),
                testMode = true
        )
        PaymentSdk.initalize(this, configuration)
//        PaymentSdk.initalize(BuildConfig.newBsApiKey, "https://payment-dev.mblb.net/api/", this, setOf(BsPayoneIntegration, BraintreeIntegration), true)

        val textColor: Int = R.color.textColor
        val backgroundColor: Int = R.color.backgroundColor
        val buttonColor: Int = R.color.buttonColor
        val buttonTextColor: Int = R.color.buttonTextColor
        val cellBackgroundColor: Int = R.color.cellBackgroundColor
        val mediumEmphasisColor: Int = R.color.mediumEmphasisColor

        val customizationPreference = PaymentUIConfiguration(
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