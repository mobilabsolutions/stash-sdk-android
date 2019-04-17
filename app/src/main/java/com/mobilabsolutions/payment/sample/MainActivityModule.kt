package com.mobilabsolutions.payment.sample

import com.mobilabsolutions.payment.sample.registration.RegistrationFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Module(includes = arrayOf(RegistrationFragmentModule::class))
abstract class MainActivityModule {
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity
}