package com.mobilabsolutions.payment.android.newapi

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.google.gson.Gson
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.internal.*
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.bspayone.BsPayoneModule
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.hypercharge.HyperchargeModule
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.oldbspayone.OldBsPayoneModule
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.psppaypal.PayPalActivityCustomization
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
//import com.tspoon.traceur.Traceur
import dagger.Component
import io.reactivex.rxkotlin.subscribeBy
import org.junit.Assert
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.`when`
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.threeten.bp.LocalDate
import java.net.URL
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import javax.inject.Singleton


/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(Base64::class)
@PowerMockIgnore("javax.net.ssl.*")
class BsPayoneApiTest {
    val testPublicKey = "PD-PO-nhnEiKIFQiZeVjGCM0HZY3xvaI"
    private val MOBILAB_BE_URL = "https://webhook.site/2c04923a-f96a-40b7-a43c-fa756e5e993d/"
    private val OLD_BS_PAYONE_URL = "https://test.soap.bs-card-service.com/soap-api/"
    private val NEW_BS_PAYONE_URL = "https://webhook.site/c83aff71-db72-414e-9fda-17eaacb714ac/"

    val classLoader = this.javaClass.classLoader

    val registerCreditCardMLBackendResponse =
            classLoader.getResourceAsStream("backend/PayoneRegisterCreditCardSuccessResponse.json")
                    .bufferedReader().use { it.readText() }
    val cardVerificationSuccessResponse =
            classLoader.getResourceAsStream("CardVerificationSuccessResponse.json")
                    .bufferedReader().use { it.readText() }
    val cardVerificationErrorResponse =
            classLoader.getResourceAsStream("CardVerificationErrorResponse.json")
                    .bufferedReader().use { it.readText() }
    val cardVerificationInvalidResponse =
            classLoader.getResourceAsStream("CardVerificationInvalidResponse.json")
                    .bufferedReader().use { it.readText() }

    @Singleton
    @Component(modules = [SslSupportModule::class, PaymentSdkModule::class, OldBsPayoneModule::class, HyperchargeModule::class, BsPayoneModule::class])
    internal interface UnitTestPaymentSdkComponent : PaymentSdkComponent {
        fun injectTest(test: BsPayoneApiTest)
    }

    @Inject
    lateinit var registrationManager: NewRegistrationManager

    @Inject
    lateinit var uiCustomizationManager: UiCustomizationManager

    @Inject
    internal lateinit var newUiCustomizationManager: NewUiCustomizationManager

    lateinit var payoneMockWebServer : MockWebServer
    lateinit var backendMockWebServer : MockWebServer

    lateinit var payoneBaseUrl : URL
    lateinit var backendBaseUrl : URL

    val validCreditCardData = CreditCardData(
            "4111111111111111",
            LocalDate.of(2021, 1, 1),
            "123",
            "Holder Holderman"
    )

    val billingData = BillingData.fromName("Holder holderman")

    var applicationContext : Application = PowerMockito.mock(Application::class.java)

    lateinit var sharedPreferences: SharedPreferences
    lateinit var mockedApplication : Application
    lateinit var mockedEditor : SharedPreferences.Editor



    @Before
    fun setUp() {
        PowerMockito.mockStatic(Base64::class.java)
        `when`(Base64.encodeToString(any(), anyInt())).thenAnswer({ invocation -> String(java.util.Base64.getEncoder().encode(invocation.getArguments()[0] as ByteArray)) })
        `when`(Base64.encode(any(), anyInt())).thenAnswer({ invocation -> java.util.Base64.getEncoder().encode(invocation.getArguments()[0] as ByteArray) })
        `when`(Base64.decode(anyString(), anyInt())).thenAnswer({ invocation -> java.util.Base64.getMimeDecoder().decode(invocation.getArguments()[0] as String) })
        //Traceur.enableLogging()



        payoneMockWebServer = MockWebServer()
        payoneMockWebServer.start()

        backendMockWebServer = MockWebServer()
        backendMockWebServer.start()

        payoneBaseUrl = payoneMockWebServer.url("").url()
        println("Payone base url: ${payoneBaseUrl}")

        backendBaseUrl = backendMockWebServer.url("").url()
        println("Backend base url: ${backendBaseUrl}")

        sharedPreferences = PowerMockito.mock(SharedPreferences::class.java)
        mockedApplication = PowerMockito.mock(Application::class.java)
        mockedEditor = PowerMockito.mock(SharedPreferences.Editor::class.java)

        PowerMockito.`when`(mockedApplication.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)
        PowerMockito.`when`(sharedPreferences.edit()).thenReturn(mockedEditor)
        PowerMockito.`when`(mockedEditor.commit()).thenReturn(true)
        PowerMockito.`when`(mockedEditor.putString(anyString(), anyString())).thenReturn(mockedEditor)


    }


    @Test
    fun testCardRegistrationSuccess() {
        val latch = CountDownLatch(1)

        backendMockWebServer.enqueue(MockResponse().setBody(registerCreditCardMLBackendResponse))
        backendMockWebServer.enqueue(MockResponse())
        payoneMockWebServer.enqueue(MockResponse().setBody(cardVerificationSuccessResponse))

        val context = mockedApplication
        val graph = DaggerBsPayoneApiTest_UnitTestPaymentSdkComponent.builder()
                .sslSupportModule(SslSupportModule(null, null))
                .paymentSdkModule(PaymentSdkModule(testPublicKey, backendBaseUrl.toString(), context))
                .oldBsPayoneModule(OldBsPayoneModule(OLD_BS_PAYONE_URL))
                .hyperchargeModule(HyperchargeModule())
                .bsPayoneModule(BsPayoneModule(payoneBaseUrl.toString()))
                .build()
        graph.injectTest(this)

        registrationManager.registerCreditCard(validCreditCardData, billingData).subscribeBy(
                onSuccess = { alias ->
                    System.out.print("Alias -> $alias" )
                    Assert.assertEquals(alias, "PSEUDO-CARD-PAN")
                    latch.countDown()
                },
                onError = {
                    println("Failed")
                    Assert.fail("Registration failed ${it.message}")
                    latch.countDown()
                    it.printStackTrace()
                }
        )

        latch.await()

    }

    @Test
    fun testCardRegistrationError() {
        val latch = CountDownLatch(1)


        backendMockWebServer.enqueue(MockResponse().setBody(registerCreditCardMLBackendResponse))
        payoneMockWebServer.enqueue(MockResponse().setBody(cardVerificationErrorResponse))

        val context = mockedApplication
        val graph = DaggerBsPayoneApiTest_UnitTestPaymentSdkComponent.builder()
                .sslSupportModule(SslSupportModule(null, null))
                .paymentSdkModule(PaymentSdkModule(testPublicKey, backendBaseUrl.toString(), context))
                .oldBsPayoneModule(OldBsPayoneModule(OLD_BS_PAYONE_URL))
                .hyperchargeModule(HyperchargeModule())
                .bsPayoneModule(BsPayoneModule(payoneBaseUrl.toString()))
                .build()
        graph.injectTest(this)

        registrationManager.registerCreditCard(validCreditCardData, billingData).subscribeBy(
                onSuccess = { alias ->

                    Assert.fail("Success not expected in this test case")
                    latch.countDown()
                },
                onError = {
                    assertTrue(it.message!!.startsWith("Unknown error"))
                    latch.countDown()
                }
        )

        latch.await()

    }

    @Test
    fun testCardRegistrationInvalid() {
        val latch = CountDownLatch(1)

        backendMockWebServer.enqueue(MockResponse().setBody(registerCreditCardMLBackendResponse))
        payoneMockWebServer.enqueue(MockResponse().setBody(cardVerificationInvalidResponse))

        val context = mockedApplication
        val graph = DaggerBsPayoneApiTest_UnitTestPaymentSdkComponent.builder()
                .sslSupportModule(SslSupportModule(null, null))
                .paymentSdkModule(PaymentSdkModule(testPublicKey, backendBaseUrl.toString(), context))
                .oldBsPayoneModule(OldBsPayoneModule(OLD_BS_PAYONE_URL))
                .hyperchargeModule(HyperchargeModule())
                .bsPayoneModule(BsPayoneModule(payoneBaseUrl.toString()))
                .build()
        graph.injectTest(this)

        registrationManager.registerCreditCard(validCreditCardData, billingData).subscribeBy(
                onSuccess = { alias ->

                    Assert.fail("Success not expected in this test case")
                    latch.countDown()
                },
                onError = {
                    println(it.message)
                    assertTrue(it.message!!.startsWith("Unknown error, invalid"))
                    latch.countDown()
                }
        )

        latch.await()

    }


    @Test
    fun testPayPalCustomizations() {
        val context = mockedApplication
        val graph = DaggerBsPayoneApiTest_UnitTestPaymentSdkComponent.builder()
                .sslSupportModule(SslSupportModule(null, null))
                .paymentSdkModule(PaymentSdkModule(testPublicKey, backendBaseUrl.toString(), context))
                .oldBsPayoneModule(OldBsPayoneModule(OLD_BS_PAYONE_URL))
                .hyperchargeModule(HyperchargeModule())
                .bsPayoneModule(BsPayoneModule(payoneBaseUrl.toString()))
                .build()
        graph.injectTest(this)
        assertNotNull(uiCustomizationManager)
        uiCustomizationManager.setPaypalRedirectActivityCustomizations(PayPalActivityCustomization(showAppBar = true, showUpNavigation = true))
        val customization = newUiCustomizationManager.getPaypalRedirectActivityCustomizations()
        assertTrue(customization.showAppBar)
        assertTrue(customization.showUpNavigation)

    }

    @Test
    fun testPayPalCustomizationPersistence() {
        val context = mockedApplication
        val graph = DaggerBsPayoneApiTest_UnitTestPaymentSdkComponent.builder()
                .sslSupportModule(SslSupportModule(null, null))
                .paymentSdkModule(PaymentSdkModule(testPublicKey, backendBaseUrl.toString(), context))
                .oldBsPayoneModule(OldBsPayoneModule(OLD_BS_PAYONE_URL))
                .hyperchargeModule(HyperchargeModule())
                .bsPayoneModule(BsPayoneModule(payoneBaseUrl.toString()))
                .build()
        graph.injectTest(this)
        val gson = Gson()
        val customization = PayPalActivityCustomization(showAppBar = true, showUpNavigation = true)
        val customizationString = gson.toJson(customization)
        PowerMockito.`when`(sharedPreferences.getString(eq(NewUiCustomizationManager.PAYPAL_CUSTOMIZATION_KEY), anyString())).thenReturn(customizationString)

        assertNotNull(uiCustomizationManager)
        uiCustomizationManager.setPaypalRedirectActivityCustomizations(customization)
        val preferences = context.getSharedPreferences(PaymentSdkModule.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val paypalCustomizationString = preferences.getString(NewUiCustomizationManager.PAYPAL_CUSTOMIZATION_KEY, "")
        assertNotNull(paypalCustomizationString)
        assertNotEquals(paypalCustomizationString, "")
        val paypalCustomization = gson.fromJson(paypalCustomizationString, PayPalActivityCustomization::class.java)
        assertTrue(paypalCustomization.showUpNavigation)
        assertTrue(paypalCustomization.showAppBar)
    }






}


