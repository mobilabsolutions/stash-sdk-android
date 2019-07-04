/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.sample.appinitializers

import android.app.Application

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
interface AppInitializer {
    fun init(application: Application)
}