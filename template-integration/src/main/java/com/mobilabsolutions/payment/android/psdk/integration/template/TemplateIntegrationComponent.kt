package com.mobilabsolutions.payment.android.psdk.integration.template

import com.mobilabsolutions.payment.android.psdk.internal.IntegrationScope
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import dagger.Component

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
@Component(dependencies = arrayOf(PaymentSdkComponent::class), modules = arrayOf(TemplateModule::class))
interface TemplateIntegrationComponent {
    fun inject(integration: TemplateIntegration)
}
