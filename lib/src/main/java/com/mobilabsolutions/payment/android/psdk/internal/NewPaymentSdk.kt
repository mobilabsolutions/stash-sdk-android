package com.mobilabsolutions.payment.android.psdk.internal

// //import com.tspoon.traceur.Traceur
import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.PaymentManager
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.PaymentSdkConfiguration
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import com.mobilabsolutions.payment.android.psdk.exceptions.validation.InvalidApplicationContextException
import com.mobilabsolutions.payment.android.psdk.exceptions.validation.InvalidPublicKeyException
import timber.log.Timber
import javax.inject.Inject
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import android.R
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump


/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class NewPaymentSdk(
    publicKey: String,
    url: String?,
    applicationContext: Application,
    integrationList: List<IntegrationInitialization>,
    testMode: Boolean,
    sslSocketFactory: SSLSocketFactory?,
    x509TrustManager: X509TrustManager?
) {
    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl

    @Inject
    lateinit var newRegistrationManager: NewRegistrationManager

    @Inject
    lateinit var newPaymentManager: NewPaymentManager

    @Inject
    lateinit var uiCustomizationManager: UiCustomizationManager

    val daggerGraph: PaymentSdkComponent

    var paymentMethodSet: Set<PaymentMethodType> = emptySet()

    private constructor(publicKey: String, url: String?, applicationContext: Application) : this(publicKey, url, applicationContext, emptyList(), true, null, null)

    init {
        val backendUrl = url ?: MOBILAB_BE_URL

        daggerGraph = DaggerPaymentSdkComponent.builder()
                .sslSupportModule(SslSupportModule(sslSocketFactory, x509TrustManager))
                .paymentSdkModule(PaymentSdkModule(publicKey, backendUrl, applicationContext, integrationList, testMode))
                .build()

        integrationList.map {
            val initialized = it.initialize(daggerGraph)
            val supportedMethods = initialized.getSupportedPaymentMethodDefinitions().map { it.paymentMethodType }
            supportedMethods.forEach { paymentMethodType ->
                if (paymentMethodSet.contains(paymentMethodType)) {
                    throw RuntimeException(
                            "You are trying to add integrations that handle same payment methods. " +
                                    "This is not supported at this moment. " +
                                    "Encountered when processing ${initialized.identifier} payment method $paymentMethodType")
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

//        @Synchronized
//        fun initialize(publicKey: String?, applicationContext: Application?, integrationList: List<IntegrationInitialization>, testMode : Boolean) {
//            initialize(publicKey, applicationContext, integrationList, null, null)
//        }

        @Synchronized
        fun initialize(applicationContext: Application, configuration: PaymentSdkConfiguration) {
            val integrationInitializations = configuration.integrations.map { it.create() }.toList()
            initialize(configuration.publicKey, configuration.endpoint, applicationContext, integrationInitializations, configuration.testMode, configuration.sslFactory, configuration.x509TrustManager)
        }

        @Synchronized
        fun initialize(publicKey: String?, url: String?, applicationContext: Application?, integrationList: List<IntegrationInitialization>, testMode: Boolean = false, sslSocketFactory: SSLSocketFactory?, x509TrustManager: X509TrustManager?) {
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
            AndroidThreeTen.init(applicationContext)
            NewPaymentSdk.instance = NewPaymentSdk(publicKey, url, applicationContext, integrationList, testMode, sslSocketFactory, x509TrustManager)

            NewPaymentSdk.initialized = true

            ViewPump.init(ViewPump.builder()
                    .addInterceptor(CalligraphyInterceptor(
                            CalligraphyConfig.Builder()
                                    .setDefaultFontPath("fonts/Lato-Regular.ttf")
                                    .build()))
                    .build())
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
                if (testComponent != null) {
                    return testComponent!!
                } else {
                    throw RuntimeException("Payment SDK not initialized!")
                }
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