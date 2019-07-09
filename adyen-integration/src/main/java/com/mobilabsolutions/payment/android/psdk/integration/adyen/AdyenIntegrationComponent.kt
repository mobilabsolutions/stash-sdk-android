/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk.integration.adyen

import com.mobilabsolutions.payment.android.psdk.integration.adyen.uicomponents.AdyenCreditCardDataEntryFragment
import com.mobilabsolutions.payment.android.psdk.integration.adyen.uicomponents.AdyenSepaDataEntryFragment
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationScope
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import dagger.Component

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
@Component(dependencies = [PaymentSdkComponent::class], modules = [AdyenModule::class])
interface AdyenIntegrationComponent {
    fun inject(integration: AdyenIntegration)

    fun inject(creditCardFragment: AdyenCreditCardDataEntryFragment)

    fun inject(adyenSepaDataEntryFragment: AdyenSepaDataEntryFragment)
}
