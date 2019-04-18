package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import com.mobilabsolutions.payment.android.psdk.integration.bspayone.uicomponents.CreditCardDataEntryFragment
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.uicomponents.SepaDataEntryFragment
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationScope
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import dagger.Component

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
@Component(dependencies = arrayOf(PaymentSdkComponent::class), modules = arrayOf(BsPayoneModule::class))
interface BsPayoneIntegrationComponent {
    fun inject(integration: BsPayoneIntegration)

    fun inject(creditCardFragment: CreditCardDataEntryFragment)

    fun inject(sepaDataEntryFragment: SepaDataEntryFragment)
}
