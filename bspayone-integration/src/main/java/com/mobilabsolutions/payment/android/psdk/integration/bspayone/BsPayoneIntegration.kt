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
class BsPayoneIntegration(
        paymentSdkComponent: PaymentSdkComponent,
        val url: String = BuildConfig.newBsApiUrl
) : Integration {
    override val identifier = "BS_PAYONE"

    @Inject
    lateinit var bsPayoneHandler: BsPayoneHandler


    companion object {
        var integration: BsPayoneIntegration? = null

        fun create(): IntegrationInitialization {
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
        val fakeAdditionalData = mapOf(
                "merchantId" to "42865",
                "portalId" to "2030968",
                "apiVersion" to "3.11",
                "mode" to "test",
                "request" to "creditcardcheck",
                "responseType" to "JSON",
                "hash" to "35996f45100c40d51cffedcddc471f8189fc3568c287871568dc6c8bae1c4d732ded416b502f6191fb6085a2d767ef6f",
                "accountId" to "42949"
        )
        return when (standardizedData) {
            is CreditCardRegistrationRequest -> {
                bsPayoneHandler.registerCreditCard(
                        registrationRequest.standardizedData.aliasId,
                        BsPayoneCreditCardRegistrationRequest.fromMap(fakeAdditionalData),
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



