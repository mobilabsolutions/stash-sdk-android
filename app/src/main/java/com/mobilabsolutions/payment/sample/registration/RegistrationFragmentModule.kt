package com.mobilabsolutions.payment.sample.registration

import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Module
abstract class RegistrationFragmentModule {
    @ContributesAndroidInjector
    abstract fun bindRegistrationFragment(): RegistrationFragment

}
