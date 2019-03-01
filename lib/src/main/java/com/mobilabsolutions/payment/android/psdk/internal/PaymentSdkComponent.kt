package com.mobilabsolutions.payment.android.psdk.internal

import android.app.Application
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApi
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApiV2
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.hypercharge.HyperchargeModule
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.psppaypal.PayPalRedirectActivity
import dagger.Component
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Singleton
@Component(modules = arrayOf(SslSupportModule::class, PaymentSdkModule::class, HyperchargeModule::class))
interface PaymentSdkComponent {
    fun inject(paymentSdk: NewPaymentSdk)

    fun inject(payPalRedirectActivity: PayPalRedirectActivity)

    fun provideApplication() : Application

    fun provideMobilabApi() : MobilabApi
    fun provideMobilabApiV2() : MobilabApiV2

    fun provideXmlConverterFactory() : SimpleXmlConverterFactory
    fun provideRxJava2Converter() : RxJava2CallAdapterFactory
    fun provideHttpLoggingInterceptor() : HttpLoggingInterceptor

    fun provideUiCustomizationManager() : UiCustomizationManager
}