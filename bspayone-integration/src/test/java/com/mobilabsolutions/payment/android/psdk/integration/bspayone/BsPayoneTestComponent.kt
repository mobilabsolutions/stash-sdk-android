/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import com.mobilabsolutions.payment.android.psdk.internal.StashComponent
import com.mobilabsolutions.payment.android.psdk.internal.StashModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [SslSupportModule::class, StashModule::class, BsPayoneModule::class])
interface BsPayoneTestComponent : StashComponent {
    fun injectTest(test: BsPayoneIntegrationTest)
}