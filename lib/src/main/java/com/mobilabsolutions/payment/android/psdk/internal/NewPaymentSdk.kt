package com.mobilabsolutions.payment.android.psdk.internal

import android.app.Application
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.PaymentManager
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.exceptions.validation.InvalidApplicationContextException
import com.mobilabsolutions.payment.android.psdk.exceptions.validation.InvalidPublicKeyException
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.bspayone.BsPayoneModule
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.hypercharge.HyperchargeModule
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.oldbspayone.OldBsPayoneModule
////import com.tspoon.traceur.Traceur
import timber.log.Timber
import javax.inject.Inject
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager


/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class NewPaymentSdk(publicKey: String, applicationContext: Application, sslSocketFactory: SSLSocketFactory?, x509TrustManager: X509TrustManager?) {
    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl
    val OLD_BS_PAYONE_URL: String = BuildConfig.oldBsApiUrl
    val NEW_BS_PAYONE_URL : String = BuildConfig.newBsApiUrl

    @Inject
    lateinit var newRegistrationManager: NewRegistrationManager

    @Inject
    lateinit var newPaymentManager: NewPaymentManager

    @Inject
    lateinit var paymentProvider: PaymentSdk.Provider

    @Inject
    lateinit var uiCustomizationManager : UiCustomizationManager

    val daggerGraph: PaymentSdkComponent

    constructor(publicKey: String, applicationContext : Application) : this(publicKey, applicationContext, null, null)

    init {

        daggerGraph = DaggerPaymentSdkComponent.builder()
                .sslSupportModule(SslSupportModule(sslSocketFactory, x509TrustManager))
                .paymentSdkModule(PaymentSdkModule(publicKey, MOBILAB_BE_URL, applicationContext))
                .oldBsPayoneModule(OldBsPayoneModule(OLD_BS_PAYONE_URL))
                .hyperchargeModule(HyperchargeModule())
                .bsPayoneModule(BsPayoneModule(NEW_BS_PAYONE_URL))
                .build()
        daggerGraph.inject(this)


    }




    companion object {

        private var instance: NewPaymentSdk? = null

        private var initialized = false

        internal var testComponent : PaymentSdkComponent? = null

        @Synchronized
        fun initialize(publicKey: String?, applicationContext: Application?) {
            initialize(publicKey, applicationContext, null, null)
        }

        @Synchronized
        fun initialize(publicKey: String?, applicationContext: Application?, sslSocketFactory: SSLSocketFactory?, x509TrustManager: X509TrustManager?) {
            if (publicKey == null) {
                throw InvalidPublicKeyException("Public key not supplied")
            }

            if (applicationContext == null) {
                throw InvalidApplicationContextException("Application context not supplied")
            }

            if (initialized) {
                Timber.w("Already initialized")
                return
            }

            Timber.plant(Timber.DebugTree())
//            //Traceur.enableLogging()
            NewPaymentSdk.instance = NewPaymentSdk(publicKey, applicationContext, sslSocketFactory, x509TrustManager)

            NewPaymentSdk.initialized = true
        }

        fun getRegistrationManager(): RegistrationManager {
            assertInitialized()
            return NewPaymentSdk.instance!!.newRegistrationManager
        }

        fun getPaymentManager(): PaymentManager {
            assertInitialized()
            return NewPaymentSdk.instance!!.newPaymentManager
        }

        fun getUiCustomizationManager() : UiCustomizationManager {
            assertInitialized()
            return NewPaymentSdk.instance!!.uiCustomizationManager
        }

        private fun assertInitialized() {
            if (instance == null) {
                throw RuntimeException(
                        "Payment SDK is not initialized, make sure you called initialize method before using"
                )
            }
        }

        fun getProviderFromKey(publicKey: String?): PaymentSdk.Provider {
            if (publicKey == null) {
                throw InvalidPublicKeyException("Public key not supplied")
            }
            if (publicKey.length < 3) {
                throw InvalidPublicKeyException("Public key length is not valid")
            }
            val parts = publicKey.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size == 3) {
                if (parts[1] == "BS") {
                    return PaymentSdk.Provider.OLD_BS_PAYONE
                }
                if (parts[1] == "HC") {
                    return PaymentSdk.Provider.HYPERCHARGE
                }
                if (parts[1] == "PO") {//BS2
                    return PaymentSdk.Provider.NEW_PAYONE
                }
            }
            throw InvalidPublicKeyException("Unknown provider")
        }

        internal fun getInjector() : PaymentSdkComponent {
            if (instance != null) {
                return instance!!.daggerGraph
            } else {
                return testComponent!!
            }
        }

        internal fun supplyTestComponent(testComponent: PaymentSdkComponent) {
            this.testComponent = testComponent
        }




        fun reset() {
            initialized = false
        }


    }
}