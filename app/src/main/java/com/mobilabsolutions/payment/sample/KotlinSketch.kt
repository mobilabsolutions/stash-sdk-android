package com.mobilabsolutions.payment.sample

import android.app.Activity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import java.util.UUID

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class KotlinSketch {
    val registrationManager: RegistrationManager = TODO()
    val activity: Activity = TODO()

    fun usingUi() {
        val idempotencyKey = UUID.randomUUID()
        registrationManager.registerPaymentMehodUsingUi(activity, PaymentMethodType.CC, idempotencyKey)
    }
}