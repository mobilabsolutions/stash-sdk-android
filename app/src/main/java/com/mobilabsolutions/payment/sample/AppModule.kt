package com.mobilabsolutions.payment.sample

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.mobilabsolutions.payment.android.psdk.PaymentManager
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import com.mobilabsolutions.payment.sample.payment.PaymentFragmentModule
import com.mobilabsolutions.payment.sample.registration.RegistrationFragmentModule
import com.mobilabsolutions.payment.sample.state.PaymentMethodState
import dagger.Module
import dagger.Provides
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
// @Module(includes = arrayOf(CommonModule::class, ActivityBindingModule::class, MenuModule::class, AuthModule::class, PaymentModule::class))
@Module(includes = arrayOf(MainActivityModule::class, PaymentFragmentModule::class, RegistrationFragmentModule::class))
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun providePref(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideRegistrationManager(): RegistrationManager {
        return PaymentSdk.getRegistrationManager()
    }

    @Singleton
    @Provides
    fun providePaymentManager(): PaymentManager {
        return PaymentSdk.getPaymentManager()
    }

    @Singleton
    @Provides
    fun providePaymentMethodStateSubject(): BehaviorSubject<PaymentMethodState> {
        return BehaviorSubject.createDefault(PaymentMethodState())
    }

    companion object {
        private const val PREFS_NAME = "sample_app_preference"
    }
}