package com.mobilabsolutions.payment.android.newapi

import android.app.Application
import android.content.SharedPreferences
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.integration.adyen.AdyenIntegration
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkModule
import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
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
    fun testMultipleIntegrations() {
        expectedException.expect(RuntimeException::class.java)
        PaymentSdk.initalize("123", "fakeUrl", application, setOf(BsPayoneIntegration, AdyenIntegration))
    }
}

@Singleton
@Component(modules = [SslSupportModule::class, PaymentSdkModule::class])
internal interface IntegrationTestSdkComponent : PaymentSdkComponent {
    fun injectTest(integrationTest: IntegrationTest)
}