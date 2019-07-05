/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk

import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * Payment Sdk Configuration is used for configuring the payment SDK
 *
 * * Publishable key - The publishable key linked to the merchant account you are using
 * * Endpoint - the endpoint of your instance of Payment SDK servers
 * * Integration - If you are using single integration for all payment methods you can use this parameter
 * * Integration List - If you are using several integrations, you need to provide pairs of integration to
 * payment method type
 * * [Payment Ui Configuration](PaymentUiConfiguration) - Styling for payment registration UI components so they blend better with your application
 * * Test mode - boolean flag to determine are you working in test mode or production mode
 * * sslFactory and x509TrustManager - If you need to provide specific SSL handling for lower android versions who don't support TLS 1.2 natively
 * you can do that by providing SslFactory and x509TrustManager
 *
 * If you are using Java, it is recommended to use the Builder class instead of data class constructor
 */
data class PaymentSdkConfiguration(
    val publishableKey: String,
    val endpoint: String,
    val integration: IntegrationCompanion? = null,
    val integrationList: List<Pair<IntegrationCompanion, PaymentMethodType>>? = null,
    val paymentUiConfiguration: PaymentUiConfiguration? = null,
    val testMode: Boolean = false,
    val sslFactory: SSLSocketFactory? = null,
    val x509TrustManager: X509TrustManager? = null

) {
    /**
     * Builder for Payment SDK configuration object
     */
    class Builder() {
        private var publishableKey: String = ""
        private var endpoint: String? = null
        private var integration: IntegrationCompanion? = null
        private var integrations: List<Pair<IntegrationCompanion, PaymentMethodType>>? = null
        private var paymentUIConfiguration: PaymentUiConfiguration? = null
        private var testMode: Boolean = false
        private var sslFactory: SSLSocketFactory? = null
        private var x509TrustManager: X509TrustManager? = null

        /**
         * Publishable key - The publishable key linked to the merchant account you are using
         */
        fun setPublishableKey(publishableKey: String): Builder {
            this.publishableKey = publishableKey
            return this
        }

        /**
         * Endpoint - the endpoint of your instance of Payment SDK servers
         */
        fun setEndpoint(endpoint: String): Builder {
            this.endpoint = endpoint
            return this
        }

        /**
         * Integration - If you are using single integration for all payment methods you can use this parameter
         */
        fun setIntegration(integration: IntegrationCompanion): Builder {
            this.integration = integration
            return this
        }

        /**
         * Integration List - If you are using several integrations, you need to provide mappings of integration to
         * payment method type
         */
        fun setIntegrations(integrations: List<IntegrationToPaymentMapping>): Builder {
            this.integrations = integrations.map {
                it.integration to it.paymentMethodType
            }.flatMap { pair ->
                pair.second.toList().map { pair.first to it }
            }
            return this
        }

        /**
         *  [Payment Ui Configuration](PaymentUiConfiguration) - Styling for payment registration UI components so they blend better with your application
         */
        fun setCustomization(paymentUIConfiguration: PaymentUiConfiguration?): Builder {
            this.paymentUIConfiguration = paymentUIConfiguration
            return this
        }

        /**
         * Test mode - boolean flag to determine are you working in test mode or production mode
         */
        fun setTestMode(testMode: Boolean): Builder {
            this.testMode = testMode
            return this
        }

        /**
         * sslFactory and x509TrustManager - If you need to provide specific SSL handling for lower android versions who don't support TLS 1.2 natively
         * you can do that by providing SslFactory and x509TrustManager
         */
        fun setSslSocketFactory(sslSocketFactory: SSLSocketFactory): Builder {
            this.sslFactory = sslFactory
            return this
        }

        /**
         * sslFactory and x509TrustManager - If you need to provide specific SSL handling for lower android versions who don't support TLS 1.2 natively
         * you can do that by providing SslFactory and x509TrustManager
         */
        fun setX509TrustManager(x509TrustManager: X509TrustManager): Builder {
            this.x509TrustManager = x509TrustManager
            return this
        }

        /**
         * Build and return configuration object
         */
        fun build(): PaymentSdkConfiguration {
            return PaymentSdkConfiguration(
                publishableKey,
                endpoint!!,
                integration,
                integrations,
                paymentUIConfiguration,
                testMode,
                sslFactory,
                x509TrustManager
            )
        }
    }
}

class IntegrationToPaymentMapping(val integration: IntegrationCompanion, vararg val paymentMethodType: PaymentMethodType)