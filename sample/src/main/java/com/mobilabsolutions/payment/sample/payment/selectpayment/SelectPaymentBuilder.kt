package com.mobilabsolutions.payment.sample.payment.selectpayment

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SelectPaymentBuilder {
    @ContributesAndroidInjector
    abstract fun bindPaymentFragment(): SelectPaymentFragment
}