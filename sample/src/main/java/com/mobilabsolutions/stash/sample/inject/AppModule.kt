/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.inject

import android.content.Context
import com.mobilabsolutions.stash.sample.SampleApplication
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
import com.mobilabsolutions.stash.sample.util.AppRxSchedulers
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.asCoroutineDispatcher
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
@Module(includes = [
    AppModuleBinds::class
])
object AppModule {

    @JvmStatic
    @Provides
    fun provideContext(application: SampleApplication): Context = application.applicationContext

    @JvmStatic
    @Singleton
    @Provides
    fun provideRxSchedulers(): AppRxSchedulers = AppRxSchedulers(
        io = Schedulers.io(),
        computation = Schedulers.computation(),
        main = AndroidSchedulers.mainThread()
    )

    @JvmStatic
    @Singleton
    @Provides
    fun provideCoroutineDispatchers(schedulers: AppRxSchedulers) = AppCoroutineDispatchers(
        io = schedulers.io.asCoroutineDispatcher(),
        computation = schedulers.computation.asCoroutineDispatcher(),
        main = Dispatchers.Main
    )

    @JvmStatic
    @Provides
    @Singleton
    @Named("cache")
    fun provideCacheDir(application: SampleApplication): File = application.cacheDir
}