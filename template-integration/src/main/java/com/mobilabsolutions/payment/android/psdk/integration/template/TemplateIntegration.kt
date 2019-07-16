/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk.integration.template

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.R
import com.mobilabsolutions.payment.android.psdk.internal.IdempotencyKey
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.UiRequestHandler
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class TemplateIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {

    override val identifier = name

    @Inject
    lateinit var applicationContext: Context

    companion object : IntegrationCompanion {

        var integration: TemplateIntegration? = null

        override val name = "Template"

        override val supportedPaymentMethodTypes: Set<PaymentMethodType> = setOf(PaymentMethodType.CC, PaymentMethodType.SEPA, PaymentMethodType.PAYPAL)

        override fun create(enabledPaymentMethodTypeSet: Set<PaymentMethodType>): IntegrationInitialization {
            return object : IntegrationInitialization {

                override val enabledPaymentMethodTypes = enabledPaymentMethodTypeSet

                override fun initializedOrNull(): Integration? {
                    return integration
                }

                override fun initialize(paymentSdkComponent: PaymentSdkComponent, url: String): Integration {
                    if (integration == null) {
                        if (url.isEmpty()) {
                            integration = TemplateIntegration(paymentSdkComponent)
                        } else {
                            TODO("Handle specific url here if PSP supports specific urls")
                        }
                    }
                    return integration as Integration
                }
            }
        }
    }

    val templateIntegrationComponent: TemplateIntegrationComponent

    init {
        templateIntegrationComponent = DaggerTemplateIntegrationComponent.builder()
            .paymentSdkComponent(paymentSdkComponent)
            .build()
    }

    override fun getPreparationData(method: PaymentMethodType): Single<Map<String, String>> {
        return Single.just(emptyMap())
    }

    override fun handleRegistrationRequest(
        registrationRequest: RegistrationRequest,
        idempotencyKey: IdempotencyKey
    ): Single<String> {

        // Warn if [Template] doesn't support Idempotency
        if (idempotencyKey.isUserSupplied) {
            Timber.w(applicationContext.getString(R.string.idempotency_message))
        }

        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun handlePaymentMethodEntryRequest(
        activity: AppCompatActivity,
        paymentMethodType: PaymentMethodType,
        additionalRegistrationData: AdditionalRegistrationData,
        resultObservable: Observable<UiRequestHandler.DataEntryResult>
    ): Observable<AdditionalRegistrationData> {

        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
