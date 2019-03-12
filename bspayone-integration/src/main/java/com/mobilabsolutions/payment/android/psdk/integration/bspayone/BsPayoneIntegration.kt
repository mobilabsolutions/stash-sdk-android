package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.*
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PaymentMethodUiDefinition
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.UiDetail
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.UiDetailType
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.ValidationRules
import io.reactivex.Single
import java.lang.RuntimeException
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BsPayoneIntegration private constructor(
        paymentSdkComponent: PaymentSdkComponent,
        val url: String = BuildConfig.newBsApiUrl
) : Integration {
    override val identifier = "BS_PAYONE"

    @Inject
    lateinit var bsPayoneHandler: BsPayoneHandler


    companion object : IntegrationCompanion {
        var integration: BsPayoneIntegration? = null

        override fun create(): IntegrationInitialization {
            return object : IntegrationInitialization {
                override fun initializedOrNull(): Integration? {
                    return integration
                }

                override fun initialize(paymentSdkComponent: PaymentSdkComponent, url: String): Integration {
                    if (integration == null) {
                        if (url.isEmpty()) {
                            integration = BsPayoneIntegration(paymentSdkComponent)
                        } else {
                            integration = BsPayoneIntegration(paymentSdkComponent, url)
                        }
                    }
                    return integration as Integration
                }

            }
        }
    }


    val bsPayoneIntegrationComponent: BsPayoneIntegrationComponent

    init {
        bsPayoneIntegrationComponent = DaggerBsPayoneIntegrationComponent.builder()
                .paymentSdkComponent(paymentSdkComponent)
                .bsPayoneModule(BsPayoneModule(url))
                .build()

        bsPayoneIntegrationComponent.inject(this)
    }


    override fun handleRegistrationRequest(registrationRequest: RegistrationRequest): Single<String> {
        val standardizedData = registrationRequest.standardizedData
        val additionalData = registrationRequest.additionalData
        return when (standardizedData) {
            is CreditCardRegistrationRequest -> {
                bsPayoneHandler.registerCreditCard(
                        registrationRequest.standardizedData.aliasId,
                        BsPayoneRegistrationRequest.fromMap(additionalData.extraData),
                        standardizedData.creditCardData
                )
            }
            is SepaRegistrationRequest -> {
                bsPayoneHandler.registerSepa(
                        registrationRequest.standardizedData.aliasId,
                        standardizedData.sepaData,
                        standardizedData.billingData
                )
            }
            else -> {
                throw RuntimeException("Unsupported payment method")
            }
        }
    }

    val creditCardNumber = UiDetail(
            identifier = "ccn",
            hint = "4211...",
            title = "Credit card number",
            type = UiDetailType.NUMBER,
            validationRules = object : ValidationRules {
                override fun verify(data: String): Pair<Boolean, String> {
                    return Pair(data.length > 0, "")
                }
            }
    )

    val creditCardUIDefinition = PaymentMethodUiDefinition(
            paymentMethodName = "CreditCard",
            paymentMethodType = PaymentMethodType.CREDITCARD,
            uiDetailList = listOf(creditCardNumber)
    )

    override fun getPaymentMethodUiDefinitions(): List<PaymentMethodUiDefinition> {
        return listOf(creditCardUIDefinition)
    }


}



