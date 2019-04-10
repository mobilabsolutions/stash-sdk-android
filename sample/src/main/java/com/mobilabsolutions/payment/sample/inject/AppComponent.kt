package com.mobilabsolutions.payment.sample.inject

import com.mobilabsolutions.payment.sample.SampleApplication
import com.mobilabsolutions.payment.sample.main.MainBuilder
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
    MainBuilder::class
])
interface AppComponent : AndroidInjector<SampleApplication> {
    @Component.Factory
    abstract class Builder : AndroidInjector.Factory<SampleApplication>
}