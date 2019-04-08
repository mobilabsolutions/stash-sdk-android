package com.mobilabsolutions.payment.sample.main.payment

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
@Module
abstract class PaymentBuilder {
    @ContributesAndroidInjector
    abstract fun bindPaymentFragment(): PaymentFragment
}