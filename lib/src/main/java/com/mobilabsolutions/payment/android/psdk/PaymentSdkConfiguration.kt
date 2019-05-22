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
    val integration : IntegrationCompanion? = null,
    val integrations: Map<IntegrationCompanion, PaymentMethodType>? = null,
    val customizationPreference: CustomizationPreference? = null,
    val testMode: Boolean = false,
    val sslFactory: SSLSocketFactory? = null,
    val x509TrustManager: X509TrustManager? = null

) {
    class Builder() {
        val publishableKey: String,
        val endpoint: String? = null,
        val integration : IntegrationCompanion? = null,
        val integrations: Map<IntegrationCompanion, PaymentMethodType>? = null,
        val customizationPreference: CustomizationPreference? = null,
        val testMode: Boolean = false,
        val sslFactory: SSLSocketFactory? = null,
        val x509TrustManager: X509TrustManager? = null

        fun setPublishableKey(publishableKey : String){

            return this
        }

        fun setEndpoint(endpoint: String): Builder {

            return this
        }
        fun setIntegration(integration: IntegrationCompanion): Builder {

            return this
        }
        fun setIntegrations(integrations: Map<IntegrationCompanion, PaymentMethodType>): Builder {

            return this
        }

        fun setCustomization

        fun setTestMode(testMode: Boolean): Builder {

            return this
        }
        fun setSslSocketFactory(sslSocketFactory: SSLSocketFactory): Builder {

            return this
        }

        fun setX509TrustManager(x509TrustManager: X509TrustManager): Builder {

            return this
        }

        fun build(): PaymentSdkConfiguration {
            return configuration
        }
    }
}