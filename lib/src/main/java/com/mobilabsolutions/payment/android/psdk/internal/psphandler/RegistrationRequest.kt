package com.mobilabsolutions.payment.android.psdk.internal.psphandler

import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class RegistrationRequest (
    val standardizedData : StandardizedData,
    val additionalData : AdditionalRegistrationData
)

interface StandardizedData {
    val aliasId : String
}



data class CreditCardRegistrationRequest(
        val creditCardData: CreditCardData,
        override val aliasId: String
) : StandardizedData

data class SepaRegistrationRequest(
        val sepaData: SepaData,
        val billingData: BillingData,
        override val aliasId: String
) : StandardizedData