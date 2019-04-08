package com.mobilabsolutions.payment.sample.main.register

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
@Module
abstract class RegisterBuilder {
    @ContributesAndroidInjector
    abstract fun bindRegisterFragment(): RegisterFragment
}