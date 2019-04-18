package com.mobilabsolutions.payment.sample.data

import com.mobilabsolutions.payment.sample.data.repositories.paymentmethod.PaymentMethodRepository
import com.mobilabsolutions.payment.sample.data.repositories.paymentmethod.PaymentMethodRepositoryImpl
import dagger.Binds
import dagger.Module

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
@Module
abstract class RepositoryBindingModule {
    @Binds
    abstract fun bind(source: PaymentMethodRepositoryImpl): PaymentMethodRepository
}