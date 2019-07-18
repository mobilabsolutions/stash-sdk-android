/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk.integration.adyen

import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import com.mobilabsolutions.payment.android.psdk.internal.StashComponent
import com.mobilabsolutions.payment.android.psdk.internal.StashModule
import dagger.Component
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 30-05-2019.
 */
@Singleton
@Component(modules = [SslSupportModule::class, StashModule::class, AdyenModule::class])
internal interface AdyenIntegrationTestComponent : StashComponent {
    fun injectTest(test: AdyenIntegrationTest)
}