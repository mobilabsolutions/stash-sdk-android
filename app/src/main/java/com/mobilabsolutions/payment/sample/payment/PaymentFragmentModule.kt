package com.mobilabsolutions.payment.sample.payment

import com.mobilabsolutions.payment.android.psdk.PaymentManager
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Module
abstract class PaymentFragmentModule {
    @ContributesAndroidInjector
    abstract fun bindPaymentFragment() : PaymentFragment


}