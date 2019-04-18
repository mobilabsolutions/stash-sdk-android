package com.mobilabsolutions.payment.sample.payment

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Module
abstract class PaymentFragmentModule {
    @ContributesAndroidInjector
    abstract fun bindPaymentFragment(): PaymentFragment
}