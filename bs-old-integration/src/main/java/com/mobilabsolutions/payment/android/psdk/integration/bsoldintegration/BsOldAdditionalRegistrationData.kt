package com.mobilabsolutions.payment.android.psdk.integration.stripeintegration

import com.mobilabsolutions.payment.android.psdk.internal.psphandler.AdditionalRegistrationData

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BsOldAdditionalRegistrationData : AdditionalRegistrationData {
    override fun getAdditionalData(): Map<String, String> {
        return emptyMap()
    }

}