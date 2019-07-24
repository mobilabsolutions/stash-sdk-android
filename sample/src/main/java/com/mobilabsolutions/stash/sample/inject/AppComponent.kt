/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.inject

import com.mobilabsolutions.stash.sample.SampleApplication
import com.mobilabsolutions.stash.sample.data.DataModule
import com.mobilabsolutions.stash.sample.main.MainBuilder
import com.mobilabsolutions.stash.sample.network.NetworkModule
import com.mobilabsolutions.stash.sample.payments.PaymentBuilder
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    AppAssistedModule::class,
    DataModule::class,
    NetworkModule::class,
    MainBuilder::class,
    PaymentBuilder::class
])
interface AppComponent : AndroidInjector<SampleApplication> {
    @Component.Factory
    abstract class Builder : AndroidInjector.Factory<SampleApplication>
}