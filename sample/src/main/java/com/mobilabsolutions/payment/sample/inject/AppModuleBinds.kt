package com.mobilabsolutions.payment.sample.inject

import com.mobilabsolutions.payment.sample.appinitializers.*
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
    abstract fun providePaymentSdkInitializer(bind: PaymentSdkInitializer): AppInitializer

}