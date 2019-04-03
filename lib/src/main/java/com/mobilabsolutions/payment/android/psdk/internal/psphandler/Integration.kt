package com.mobilabsolutions.payment.android.psdk.internal.psphandler

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodDefinition
import io.reactivex.Single

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
typealias PspIdentifier = String

interface Integration {
    val identifier : PspIdentifier

    fun handleRegistrationRequest(registrationRequest: RegistrationRequest) : Single<String>

    fun getSupportedPaymentMethodDefinitions() : List<PaymentMethodDefinition>

    fun handlePaymentMethodEntryRequest(activity : AppCompatActivity, paymentMethodDefinition: PaymentMethodDefinition) : Single<Map<String, String>>

    fun supportsPaymentMethods(methodType: PaymentMethodType) : Boolean {
        return getSupportedPaymentMethodDefinitions().filter { it.paymentMethodType == methodType }.isNotEmpty()
    }

}

interface IntegrationCompanion {

    fun create() : IntegrationInitialization

}

