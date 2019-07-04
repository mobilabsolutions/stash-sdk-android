/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.sample.main.checkout

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
@Module
abstract class CheckoutBuilder {
    @ContributesAndroidInjector
    abstract fun bindCheckoutFragment(): CheckoutFragment
}