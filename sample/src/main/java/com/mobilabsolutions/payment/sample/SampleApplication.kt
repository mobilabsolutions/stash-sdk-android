package com.mobilabsolutions.payment.sample

import com.facebook.stetho.Stetho
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreeIntegration
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import com.mobilabsolutions.payment.sample.inject.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
class SampleApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        PaymentSdk.initalize(BuildConfig.newBsApiKey, BuildConfig.mobilabBackendUrl, this, setOf(BsPayoneIntegration, BraintreeIntegration), true)

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }
}