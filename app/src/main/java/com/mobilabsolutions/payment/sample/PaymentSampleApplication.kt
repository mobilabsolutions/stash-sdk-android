package com.mobilabsolutions.payment.sample

import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobilabsolutions.commonsv3_dagger.mvp.presenter.DaggerPresenterManager
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreeIntegration
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PaymentSampleApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        PaymentSdk.initalize(BuildConfig.newBsApiKey, this, setOf(BsPayoneIntegration, BraintreeIntegration), true)


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