/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.payments

import androidx.lifecycle.ViewModel
import com.mobilabsolutions.stash.sample.inject.ViewModelBuilder
import com.mobilabsolutions.stash.sample.inject.ViewModelKey
import com.mobilabsolutions.stash.sample.payments.selectpayment.SelectPaymentBuilder
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class PaymentBuilder {
    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class,
        SelectPaymentBuilder::class
    ])
    abstract fun paymentActivity(): PaymentActivity

    @Binds
    @IntoMap
    @ViewModelKey(PaymentActivityViewModel::class)
    abstract fun bindPaymentActivityViewModel(viewModel: PaymentActivityViewModel): ViewModel
}