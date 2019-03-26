package com.mobilabsolutions.payment.android.psdk.internal

////import com.tspoon.traceur.Traceur
import android.app.Application
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.PaymentManager
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.exceptions.validation.InvalidApplicationContextException
import com.mobilabsolutions.payment.android.psdk.exceptions.validation.InvalidPublicKeyException
import timber.log.Timber
import javax.inject.Inject
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager


/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class NewPaymentSdk(
        publicKey: String,
        applicationContext: Application,
        integrationList : List<IntegrationInitialization>,
        sslSocketFactory: SSLSocketFactory?,
        x509TrustManager: X509TrustManager?) {
    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl
    val OLD_BS_PAYONE_URL: String = BuildConfig.oldBsApiUrl



    @Inject
    lateinit var newRegistrationManager: NewRegistrationManager

    @Inject
    lateinit var newPaymentManager: NewPaymentManager

    @Inject
    lateinit var uiCustomizationManager: UiCustomizationManager

    val daggerGraph: PaymentSdkComponent

    var paymentMethodSet : Set<PaymentMethodType> = emptySet()

    private constructor(publicKey: String, applicationContext: Application) : this(publicKey, applicationContext, emptyList(),null, null)

    init {


        daggerGraph = DaggerPaymentSdkComponent.builder()
                .sslSupportModule(SslSupportModule(sslSocketFactory, x509TrustManager))
                .paymentSdkModule(PaymentSdkModule(publicKey, MOBILAB_BE_URL, applicationContext, integrationList))
                .build()

        integrationList.map {
            val initialized = it.initialize(daggerGraph)
            val supportedMethods = initialized.getSupportedPaymentMethodDefinitions().map { it.paymentMethodType }
            supportedMethods.forEach { paymentMethodType ->
                if (paymentMethodSet.contains(paymentMethodType)) {
                    throw RuntimeException("You are trying to add integrations that support same payment methods. This is not supported at this moment")
                } else {
                    paymentMethodSet += paymentMethodType
                }
            }
            initialized
        }



        daggerGraph.inject(this)





    }


    companion object {

        private var instance: NewPaymentSdk? = null

        private var initialized = false

        internal var testComponent: PaymentSdkComponent? = null

        @Synchronized
        fun initialize(publicKey: String?, applicationContext: Application?, integrationList : List<IntegrationInitialization>) {
            initialize(publicKey, applicationContext, integrationList, null, null)
        }

        @Synchronized
        fun initialize(publicKey: String?, applicationContext: Application?, integrationList : List<IntegrationInitialization>, sslSocketFactory: SSLSocketFactory?, x509TrustManager: X509TrustManager?) {
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
            NewPaymentSdk.instance = NewPaymentSdk(publicKey, applicationContext, integrationList, sslSocketFactory, x509TrustManager)

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

        fun getUiCustomizationManager(): UiCustomizationManager {
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

        internal fun getInjector(): PaymentSdkComponent {
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