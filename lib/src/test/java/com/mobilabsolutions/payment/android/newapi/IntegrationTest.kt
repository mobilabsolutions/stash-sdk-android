/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.newapi

import android.app.Application
import android.content.SharedPreferences
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.Stash
import com.mobilabsolutions.payment.android.psdk.StashConfiguration
import com.mobilabsolutions.payment.android.psdk.exceptions.base.ConfigurationException
import com.mobilabsolutions.payment.android.psdk.integration.adyen.AdyenIntegration
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreeIntegration
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import com.mobilabsolutions.payment.android.psdk.internal.StashComponent
import com.mobilabsolutions.payment.android.psdk.internal.StashModule
import dagger.Component
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.modules.junit4.PowerMockRunner
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@RunWith(PowerMockRunner::class)
@PowerMockIgnore("javax.net.ssl.*", "android.util.Log.**")
class IntegrationTest {

    @Rule
    var expectedException = ExpectedException.none()

    internal var application = PowerMockito.mock(Application::class.java)
    lateinit var sharedPreferences: SharedPreferences
    lateinit var mockedEditor: SharedPreferences.Editor

    @Before
    fun setUp() {
        sharedPreferences = PowerMockito.mock(SharedPreferences::class.java)
        mockedEditor = PowerMockito.mock(SharedPreferences.Editor::class.java)

        PowerMockito.`when`(application.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(sharedPreferences)
        PowerMockito.`when`(sharedPreferences.edit()).thenReturn(mockedEditor)
        PowerMockito.`when`(mockedEditor.commit()).thenReturn(true)
        PowerMockito.`when`(mockedEditor.putString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(mockedEditor)
    }

    @Test
    fun testDuplicateIntegrations() {
        expectedException.expect(ConfigurationException::class.java)
        val stashConfiguration = StashConfiguration(
            publishableKey = "123",
            endpoint = "https://fakeUrl",
            integrationList = listOf(
                BsPayoneIntegration to PaymentMethodType.SEPA,
                AdyenIntegration to PaymentMethodType.SEPA
            )
        )
        Stash.initialize(application, stashConfiguration)
    }

    @Test
    fun testMultipleIntegrationsWithUnsupportedPaymentMethods() {
        expectedException.expect(ConfigurationException::class.java)
        val stashConfiguration = StashConfiguration(
            publishableKey = "123",
            endpoint = "https://fakeUrl",
            integrationList = listOf(
                BraintreeIntegration to PaymentMethodType.SEPA,
                BraintreeIntegration to PaymentMethodType.CC
            )
        )
        Stash.initialize(application, stashConfiguration)
    }

    @Test
    fun testEmptyIntegrationListProvided() {
        expectedException.expect(ConfigurationException::class.java)
        val stashConfiguration = StashConfiguration(
            publishableKey = "123",
            endpoint = "https://fakeUrl",
            integrationList = listOf()
        )
        Stash.initialize(application, stashConfiguration)
    }

    @Test
    fun testNoIntegrationProvided() {
        expectedException.expect(ConfigurationException::class.java)
        val stashConfiguration = StashConfiguration(
            publishableKey = "123",
            endpoint = "https://fakeUrl"
        )
        Stash.initialize(application, stashConfiguration)
    }

    @Test
    fun testBothIntegrationAndIntegrationListProvided() {
        expectedException.expect(ConfigurationException::class.java)
        val stashConfiguration = StashConfiguration(
            publishableKey = "123",
            endpoint = "https://fakeUrl",
            integration = BraintreeIntegration,
            integrationList = listOf(
                BraintreeIntegration to PaymentMethodType.SEPA,
                BraintreeIntegration to PaymentMethodType.CC
            )
        )
        Stash.initialize(application, stashConfiguration)
    }
}

@Singleton
@Component(modules = [SslSupportModule::class, StashModule::class])
internal interface IntegrationTestSdkComponent : StashComponent {
    fun injectTest(integrationTest: IntegrationTest)
}