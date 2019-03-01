package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.CreditCardRegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.SepaRegistrationRequest
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BsPayoneIntegration(paymentSdkComponent: PaymentSdkComponent) : Integration {
    override val identifier = "Stripe"


    val NEW_BS_PAYONE_URL: String = BuildConfig.newBsApiUrl

    @Inject
    lateinit var bsPayoneHandler: BsPayoneHandler


    companion object {
        var integration : BsPayoneIntegration? = null

        fun create() : IntegrationInitialization {
            return object : IntegrationInitialization {
                override fun initializedOrNull(): Integration? {
                    return integration
                }

                override fun initialize(paymentSdkComponent: PaymentSdkComponent): Integration {
                    if (integration == null) {
                        integration = BsPayoneIntegration(paymentSdkComponent)
                    }
                    return integration as Integration
                }

            }
        }
    }


    val bsPayoneIntegrationComponent : BsPayoneIntegrationComponent

    init {
        bsPayoneIntegrationComponent = DaggerBsPayoneIntegrationComponent.builder()
                .paymentSdkComponent(paymentSdkComponent)
                .bsPayoneModule(BsPayoneModule(NEW_BS_PAYONE_URL))
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
                        BsPayoneCreditCardRegistrationRequest.fromMap(additionalData.extraData),
                        standardizedData.creditCardData
                )
            }
            is SepaRegistrationRequest -> {
                TODO()
            }
            else -> {
                TODO()
            }
        }


        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}



