package com.mobilabsolutions.payment.sample

import android.app.Application
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import org.threeten.bp.LocalDate

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class KotlinSketch {
    fun bla() {
        val billingData = BillingData.fromEmail("bla@bla.com")
        val builtBillingData = BillingData.Builder().build()
        val context: Application? = null

        PaymentSdk.initalize(BuildConfig.newBsApiKey, context, BsPayoneIntegration)
        val registrationManager = PaymentSdk.getRegistrationManager()

        val creditCardData = CreditCardData(
                "123",
                LocalDate.of(2011, 11, 1),
                "123",
                "Bla"
        )

        registrationManager.registerCreditCard(creditCardData)
                .subscribe(
                        { alias ->
                            //Handle alias
                        },
                        { error ->
                            //Handle error
                        }
                )

    }
}