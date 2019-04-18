package com.mobilabsolutions.payment.sample.appinitializers

import android.app.Application
import com.facebook.stetho.Stetho
import com.mobilabsolutions.payment.sample.BuildConfig
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class DebugToolsInitializer @Inject constructor() : AppInitializer {
    override fun init(application: Application) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(application)
        }
    }
}