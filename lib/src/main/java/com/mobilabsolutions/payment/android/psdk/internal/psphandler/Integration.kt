package com.mobilabsolutions.payment.android.psdk.internal.psphandler

import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.Idempotency
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.UiRequestHandler
import io.reactivex.Observable
import io.reactivex.Single

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
typealias PspIdentifier = String

interface Integration {
    val identifier: PspIdentifier

    fun getPreparationData(method: PaymentMethodType): Single<Map<String, String>>

    fun handleRegistrationRequest(
        registrationRequest: RegistrationRequest,
        idempotency: Idempotency
    ): Single<String>

    fun handlePaymentMethodEntryRequest(
        activity: AppCompatActivity,
        paymentMethodType: PaymentMethodType,
        additionalRegistrationData: AdditionalRegistrationData,
        resultObservable: Observable<UiRequestHandler.DataEntryResult>,
        idempotency: Idempotency
    ): Observable<AdditionalRegistrationData>
}

interface IntegrationCompanion {

    val supportedPaymentMethodTypes: Set<PaymentMethodType>

    fun create(enabledPaymentMethodTypeSet: Set<PaymentMethodType>): IntegrationInitialization
}
