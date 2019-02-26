package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration

import com.mobilabsolutions.payment.android.psdk.internal.IntegrationScope
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import dagger.Component

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
@Component(dependencies = arrayOf(PaymentSdkComponent::class), modules = arrayOf(BsOldModule::class))
interface BsOldIntegrationComponent {
    fun inject(integration : BsOldIntegration)

    @Component.Builder
    interface Builder {
        fun coreComponent(paymentSdkComponent: PaymentSdkComponent) : Builder
        fun bsOldModule(bsOldModule: BsOldModule) : Builder
        fun build() : BsOldIntegrationComponent
    }

}
