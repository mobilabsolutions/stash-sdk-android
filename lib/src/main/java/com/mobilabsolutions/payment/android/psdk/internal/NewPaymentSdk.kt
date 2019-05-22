package com.mobilabsolutions.payment.android.psdk.internal

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.CustomizationPreference
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.PaymentSdkConfiguration
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.exceptions.base.ConfigurationException
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import timber.log.Timber
import javax.inject.Inject
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class NewPaymentSdk(
    publicKey: String,
    url: String?,
    applicationContext: Application,
    integrationMap: Map<IntegrationCompanion, Set<PaymentMethodType>>,
    testMode: Boolean,
    sslSocketFactory: SSLSocketFactory?,
    x509TrustManager: X509TrustManager?
) {
    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl

    @Inject
    lateinit var newRegistrationManager: NewRegistrationManager

    @Inject
    internal lateinit var uiCustomizationManager: UiCustomizationManager

    val daggerGraph: PaymentSdkComponent

    init {
        val backendUrl = url ?: MOBILAB_BE_URL

        val processedPaymentMethodTypes: MutableSet<PaymentMethodType> = mutableSetOf()
        integrationMap.forEach { (integration, paymentMethodTypeSet) ->
            processedPaymentMethodTypes.forEach {
                if (paymentMethodTypeSet.contains(it)) {
                    throw ConfigurationException("Integration handling for ${it.name} payment method type already registered!")
                }
            }
            processedPaymentMethodTypes.addAll(paymentMethodTypeSet)
        }

        val integrationInitializationMap = integrationMap.mapKeys { it.key.create(it.value) }

        daggerGraph = DaggerPaymentSdkComponent.builder()
            .sslSupportModule(SslSupportModule(sslSocketFactory, x509TrustManager))
            .paymentSdkModule(PaymentSdkModule(publicKey, backendUrl, applicationContext, integrationInitializationMap, testMode))
            .build()

        integrationInitializationMap.forEach { (initialization, _) ->
            initialization.initialize(daggerGraph)
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

                if (initialized) {
                    throw ConfigurationException("Already initialized")
                }

                val integrationInitializationMap =
                    when {
                        integration == null && integrationList == null -> {
                            throw ConfigurationException("No integrations provided")
                        }
                        integration != null && integrationList != null -> {
                            throw ConfigurationException("Both integration and integration map were supplied," +
                                " provide only one or the other")
                        }
                        integration != null -> {
                            mapOf(integration to integration.supportedPaymentMethodTypes)
                        }
                        integrationList != null -> {
                            if (integrationList.isEmpty()) {
                                throw ConfigurationException("Integration list was provided, but it was empty")
                            }
                            integrationList.toList().groupBy {
                                it.first
                            }.map {
                                Pair(it.key, it.value.map { pair -> pair.second }.toSet())
                            }.toMap()
                        }
                        else -> throw RuntimeException("This should never happen")
                    }

                Timber.plant(Timber.DebugTree())
                AndroidThreeTen.init(applicationContext)
                instance = NewPaymentSdk(publicKey, endpoint, applicationContext, integrationInitializationMap, testMode, sslFactory, x509TrustManager)

                initialized = true

                ViewPump.init(ViewPump.builder()
                    .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/Lato-Regular.ttf")
                            .build()))
                    .build())
            }
        }

        fun configureUi(customizationPreference: CustomizationPreference) {
            assertInitialized()
            instance!!.uiCustomizationManager.setCustomizationPreferences(customizationPreference)
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