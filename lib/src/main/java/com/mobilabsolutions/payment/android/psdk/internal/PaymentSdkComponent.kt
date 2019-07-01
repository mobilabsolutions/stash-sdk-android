package com.mobilabsolutions.payment.android.psdk.internal

import android.app.Application
import android.content.Context
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApi
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodChoiceFragment
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.RegistrationProcessHostActivity
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Singleton
@Component(modules = arrayOf(AndroidSupportInjectionModule::class, SslSupportModule::class, PaymentSdkModule::class))
interface PaymentSdkComponent {
    fun inject(paymentSdkImpl: PaymentSdkImpl)

    fun inject(registrationProcessHostActivity: RegistrationProcessHostActivity)

    fun inject(paymentMethodChoiceFragment: PaymentMethodChoiceFragment)

    fun provideApplication(): Application

    fun provideMobilabApi(): MobilabApi

    fun providesContext(): Context

    fun provideRxJava2Converter(): RxJava2CallAdapterFactory
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor

    fun provideUiCustomizationManager(): UiCustomizationManager
}