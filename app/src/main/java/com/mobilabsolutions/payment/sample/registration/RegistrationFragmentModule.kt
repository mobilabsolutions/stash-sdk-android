package com.mobilabsolutions.payment.sample.registration

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Module
abstract class RegistrationFragmentModule {
    @ContributesAndroidInjector
    abstract fun bindRegistrationFragment(): RegistrationFragment
}
