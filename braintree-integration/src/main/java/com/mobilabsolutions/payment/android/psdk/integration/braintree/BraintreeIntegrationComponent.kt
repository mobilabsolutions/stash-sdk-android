/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk.integration.braintree

import com.mobilabsolutions.payment.android.psdk.internal.IntegrationScope
import com.mobilabsolutions.payment.android.psdk.internal.StashComponent
import dagger.Component

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
@Component(dependencies = [StashComponent::class], modules = [BraintreeModule::class])
interface BraintreeIntegrationComponent {
    fun inject(integration: BraintreeIntegration)

    fun inject(payPalActivity: BraintreePayPalActivity)
}
