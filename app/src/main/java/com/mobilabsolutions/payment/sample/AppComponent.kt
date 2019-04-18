package com.mobilabsolutions.payment.sample

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Singleton
@Component(modules = arrayOf(AndroidSupportInjectionModule::class, AppModule::class))
interface AppComponent : AndroidInjector<DaggerApplication>, PresenterInjection {

    fun inject(app: PaymentSampleApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}