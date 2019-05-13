package com.mobilabsolutions.payment.sample.payment

import androidx.lifecycle.ViewModel
import com.mobilabsolutions.payment.sample.inject.ViewModelBuilder
import com.mobilabsolutions.payment.sample.inject.ViewModelKey
import com.mobilabsolutions.payment.sample.main.checkout.CheckoutBuilder
import com.mobilabsolutions.payment.sample.main.items.ItemsBuilder
import com.mobilabsolutions.payment.sample.main.paymentmethods.PaymentMethodsBuilder
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class PaymentBuilder {
    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class,
        ItemsBuilder::class,
        CheckoutBuilder::class,
        PaymentMethodsBuilder::class
    ])
    abstract fun mainActivity(): PaymentActivity

    @Binds
    @IntoMap
    @ViewModelKey(PaymentActivityViewModel::class)
    abstract fun bindMainActivityViewModel(viewModel: PaymentActivityViewModel): ViewModel
}