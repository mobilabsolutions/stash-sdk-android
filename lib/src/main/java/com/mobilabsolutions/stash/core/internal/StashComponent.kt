/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal

import android.app.Application
import android.content.Context
import com.mobilabsolutions.stash.core.UiCustomizationManager
import com.mobilabsolutions.stash.core.internal.api.backend.MobilabApi
import com.mobilabsolutions.stash.core.internal.uicomponents.PaymentMethodChoiceFragment
import com.mobilabsolutions.stash.core.internal.uicomponents.RegistrationProcessHostActivity
import com.mobilabsolutions.stash.core.ui.HostActivity
import com.mobilabsolutions.stash.core.ui.StashAssistedModule
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryFragment
import com.mobilabsolutions.stash.core.ui.picker.PaymentPickerFragment
import dagger.Component
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Singleton
@Component(modules = [
    SslSupportModule::class,
    StashModule::class,
    StashAssistedModule::class
])
interface StashComponent {
    fun inject(stashImpl: StashImpl)

    fun inject(registrationProcessHostActivity: RegistrationProcessHostActivity)

    fun inject(paymentMethodChoiceFragment: PaymentMethodChoiceFragment)

    fun provideApplication(): Application

    fun provideMobilabApi(): MobilabApi

    fun providesContext(): Context

    fun provideRxJava2Converter(): RxJava2CallAdapterFactory

    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor

    fun provideUiCustomizationManager(): UiCustomizationManager

    fun inject(hostActivity: HostActivity)

    fun inject(paymentPickerFragment: PaymentPickerFragment)

    fun inject(creditCardEntryFragment: CreditCardEntryFragment)
}