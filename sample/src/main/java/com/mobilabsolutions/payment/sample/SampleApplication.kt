package com.mobilabsolutions.payment.sample

import com.mobilabsolutions.payment.sample.appinitializers.AppInitializers
import com.mobilabsolutions.payment.sample.inject.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
class SampleApplication : DaggerApplication() {

    @Inject
    lateinit var initializers: AppInitializers

    override fun onCreate() {
        super.onCreate()
        initializers.init(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }
}