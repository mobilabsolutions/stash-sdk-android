package com.mobilabsolutions.payment.sample.payments.selectpayment

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SelectPaymentBuilder {
    @ContributesAndroidInjector
    abstract fun bindSelectPaymentFragment(): SelectPaymentFragment
}