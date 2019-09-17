package com.mobilabsolutions.stash.core.ui

import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryFragment
import com.mobilabsolutions.stash.core.ui.picker.PaymentPickerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
@Module
abstract class HostFragmentsInject {
    @ContributesAndroidInjector
    abstract fun bindPaymentPickerFragment(): PaymentPickerFragment

    @ContributesAndroidInjector
    abstract fun bindCreditCardEntryFragment(): CreditCardEntryFragment
}