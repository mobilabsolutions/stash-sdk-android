package com.mobilabsolutions.stash.core.ui

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
@AssistedModule
@Module(includes = [AssistedInject_StashAssistedModule::class])
abstract class StashAssistedModule