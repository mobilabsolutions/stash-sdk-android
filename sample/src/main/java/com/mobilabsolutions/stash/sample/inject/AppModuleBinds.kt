/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.inject

import com.mobilabsolutions.stash.sample.appinitializers.AppInitializer
import com.mobilabsolutions.stash.sample.appinitializers.CrashlyticsInitializer
import com.mobilabsolutions.stash.sample.appinitializers.DebugToolsInitializer
import com.mobilabsolutions.stash.sample.appinitializers.EpoxyInitializer
import com.mobilabsolutions.stash.sample.appinitializers.RxAndroidInitializer
import com.mobilabsolutions.stash.sample.appinitializers.StashInitializer
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
@Module
abstract class AppModuleBinds {
    @Binds
    @IntoSet
    abstract fun provideDebugToolsInitializer(bind: DebugToolsInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideCrashlyticsInitializer(bind: CrashlyticsInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideRxAndroidInitializer(bind: RxAndroidInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideEpoxyInitializer(bind: EpoxyInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideStashInitializer(bind: StashInitializer): AppInitializer
}