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
    val integrations: Set<IntegrationCompanion> = emptySet(),
    val testMode: Boolean = false,
    val sslFactory: SSLSocketFactory? = null,
    val x509TrustManager: X509TrustManager? = null

) {
    class Builder(val publicKey: String) {
        var configuration = PaymentSdkConfiguration(publicKey)
        fun setEndpoint(endpoint: String): Builder {
            configuration = configuration.copy(endpoint = endpoint)
            return this
        }
        fun setIntegrations(integrations: Set<IntegrationCompanion>): Builder {
            configuration = configuration.copy(integrations = integrations)
            return this
        }
        fun setTestMode(testMode: Boolean): Builder {
            configuration = configuration.copy(testMode = testMode)
            return this
        }
        fun setSslSocketFactory(sslSocketFactory: SSLSocketFactory): Builder {
            configuration = configuration.copy(sslFactory = sslSocketFactory)
            return this
        }

        fun setX509TrustManager(x509TrustManager: X509TrustManager): Builder {
            configuration = configuration.copy(x509TrustManager = x509TrustManager)
            return this
        }

        fun build(): PaymentSdkConfiguration {
            return configuration
        }
    }
}