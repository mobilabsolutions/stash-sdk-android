package com.mobilabsolutions.stash.core.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
@Module
abstract class HostInject {
    @ContributesAndroidInjector(modules = [
        HostFragmentsInject::class
    ])
    abstract fun hostActivity(): HostActivity
}

