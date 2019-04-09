package com.mobilabsolutions.payment.sample.main

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

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
@Module
abstract class MainBuilder {
    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class,
        ItemsBuilder::class,
        CheckoutBuilder::class,
        PaymentMethodsBuilder::class
    ])
    abstract fun mainActivity(): MainActivity

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainActivityViewModel(viewModel: MainActivityViewModel): ViewModel
}