package com.mobilabsolutions.payment.sample

import android.app.Activity
import android.app.Application
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import org.threeten.bp.LocalDate
import java.util.*

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class KotlinSketch {
    val registrationManager : RegistrationManager = TODO()
    val activity : Activity = TODO()

    fun usingUi() {
        val idempotencyKey = UUID.randomUUID()
        registrationManager.registerPaymentMehodUsingUi(activity, PaymentMethodType.CREDITCARD, idempotencyKey)

    }

}