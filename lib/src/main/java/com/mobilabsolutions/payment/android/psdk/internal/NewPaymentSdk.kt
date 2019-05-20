package com.mobilabsolutions.payment.android.psdk.internal

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.PaymentSdkConfiguration
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.exceptions.base.ConfigurationException
import timber.log.Timber
import javax.inject.Inject
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
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
            val supportedMethods =
                    initialized.getSupportedPaymentMethodDefinitions()
                            .map { integration -> integration.paymentMethodType }
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

        private var testComponent: PaymentSdkComponent? = null

        @Synchronized
        fun initialize(applicationContext: Application, configuration: PaymentSdkConfiguration) {
            configuration.apply {
                val integrationInitializations = configuration.integrations.map { it.create() }.toList()

                if (initialized) {
                    throw ConfigurationException("Already initialized")
                }

                Timber.plant(Timber.DebugTree())
                AndroidThreeTen.init(applicationContext)
                instance = NewPaymentSdk(publicKey, endpoint, applicationContext, integrationInitializations, testMode, sslFactory, x509TrustManager)

                initialized = true

                ViewPump.init(ViewPump.builder()
                        .addInterceptor(CalligraphyInterceptor(
                                CalligraphyConfig.Builder()
                                        .setDefaultFontPath("fonts/Lato-Regular.ttf")
                                        .build()))
                        .build())
            }
        }

        fun getRegistrationManager(): RegistrationManager {
            assertInitialized()
            return instance!!.newRegistrationManager
        }

        fun getUiCustomizationManager(): UiCustomizationManager {
            assertInitialized()
            return instance!!.uiCustomizationManager
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