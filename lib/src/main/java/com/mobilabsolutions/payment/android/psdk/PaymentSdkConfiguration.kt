package com.mobilabsolutions.payment.android.psdk

import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * @author [Ugljesa Jovanovic](ugi@mobilabsolutions.com)
 */
data class PaymentSdkConfiguration(
    val publicKey: String,
    val endpoint: String? = null,
    val integration: IntegrationCompanion? = null,
    val integrationMap: Map<IntegrationCompanion, PaymentMethodType>? = null,
    val customizationPreference: CustomizationPreference? = null,
    val testMode: Boolean = false,
    val sslFactory: SSLSocketFactory? = null,
    val x509TrustManager: X509TrustManager? = null

) {
    class Builder() {
        var publishableKey: String = ""
        var endpoint: String? = null
        var integration: IntegrationCompanion? = null
        var integrations: Map<IntegrationCompanion, PaymentMethodType>? = null
        var customizationPreference: CustomizationPreference? = null
        var testMode: Boolean = false
        var sslFactory: SSLSocketFactory? = null
        var x509TrustManager: X509TrustManager? = null

        fun setPublishableKey(publishableKey: String): Builder {
            this.publishableKey = publishableKey
            return this
        }

        fun setEndpoint(endpoint: String): Builder {
            this.endpoint = endpoint
            return this
        }

        fun setIntegration(integration: IntegrationCompanion): Builder {
            this.integration = integration
            return this
        }

        fun setIntegrations(integrations: Map<IntegrationCompanion, PaymentMethodType>): Builder {
            this.integrations = integrations
            return this
        }

        fun setCustomization(customizationPreference: CustomizationPreference?) : Builder {
            this.customizationPreference = customizationPreference
            return this
        }

        fun setTestMode(testMode: Boolean): Builder {
            this.testMode = testMode
            return this
        }

        fun setSslSocketFactory(sslSocketFactory: SSLSocketFactory): Builder {
            this.sslFactory = sslFactory
            return this
        }

        fun setX509TrustManager(x509TrustManager: X509TrustManager): Builder {
            this.x509TrustManager = x509TrustManager
            return this
        }

        fun build(): PaymentSdkConfiguration {
            return PaymentSdkConfiguration(
                publishableKey,
                endpoint,
                integration,
                integrations,
                customizationPreference,
                testMode,
                sslFactory,
                x509TrustManager
            )
        }
    }
}