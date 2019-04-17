package com.mobilabsolutions.payment.sample.appinitializers

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.mobilabsolutions.payment.sample.BuildConfig
import io.fabric.sdk.android.Fabric
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class CrashlyticsInitializer @Inject constructor() : AppInitializer {
    override fun init(application: Application) {
        if (!BuildConfig.DEBUG) {
            Fabric.with(application, Crashlytics())
        }
    }
}
