package com.mobilabsolutions.stash.sample.main

import com.mobilabsolutions.stash.sample.main.checkout.CheckoutBuilder
import com.mobilabsolutions.stash.sample.main.items.ItemsBuilder
import com.mobilabsolutions.stash.sample.main.paymentmethods.PaymentMethodsBuilder
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 16-08-2019.
 */
@Module
abstract class HomeBuilder {
    @ContributesAndroidInjector(modules = [
        ItemsBuilder::class,
        CheckoutBuilder::class,
        PaymentMethodsBuilder::class
    ])
    abstract fun homeActivity(): HomeActivity
}