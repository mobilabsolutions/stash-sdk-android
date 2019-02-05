package com.mobilabsolutions.payment.android.newapi

import android.app.Application
import androidx.test.InstrumentationRegistry
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.internal.*
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.bspayone.BsPayoneModule
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.hypercharge.HyperchargeModule
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.oldbspayone.OldBsPayoneModule
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.PaymentData
//import com.tspoon.traceur.Traceur
import dagger.Component
import io.reactivex.rxkotlin.subscribeBy
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import timber.log.Timber
import java.net.URL
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import javax.inject.Singleton


/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PayPalRedirectFlowTest {
    val testPublicKey = "PD-PO-nhnEiKIFQiZeVjGCM0HZY3xvaI"
    private val MOBILAB_BE_URL = BuildConfig.mobilabBackendUrl
    private val OLD_BS_PAYONE_URL = BuildConfig.oldBsApiUrl
    //    private val NEW_BS_PAYONE_URL = "https://secure.pay1.de/client-api/"
    private val NEW_BS_PAYONE_URL = BuildConfig.newBsApiUrl


    @Inject
    lateinit var registrationManager: NewRegistrationManager

    @Inject
    lateinit var paymentManager: NewPaymentManager


    var payPalPaymentResponse = """{
  "result": {
    "mappedTransactionId" : "123",
    "redirectUrl" : "PAYPAL_REPLACE",
    "amount" : "100",
    "currency" : "EUR",
    "reason" : "Mock reason"
  }
}"""

    lateinit var payPalMockWebServer: MockWebServer
    lateinit var backendMockWebServer: MockWebServer

    lateinit var payPalBaseUrl: URL
    lateinit var backendBaseUrl: URL

    private var paymentData: PaymentData = PaymentData(
            amount = 100,
            currency = "EUR",
            customerId = "1",
            reason = "Test payment"
    )

    private var billingData: BillingData = BillingData(
            city = "Cologne",
            email = "holder@email.test",
            address1 = "Street 1",
            country = "Germany",
            firstName = "Holder",
            lastName = "Holderman",
            zip = "12345"
    )

    @Before
    fun setUp() {
        payPalMockWebServer = MockWebServer()
        payPalMockWebServer.start()

        backendMockWebServer = MockWebServer()
        backendMockWebServer.start()

        payPalBaseUrl = payPalMockWebServer.url("").url()
        println("Payone base url: ${payPalBaseUrl}")

        backendBaseUrl = backendMockWebServer.url("").url()
        println("Backend base url: ${backendBaseUrl}")

        payPalPaymentResponse = payPalPaymentResponse.replace("PAYPAL_REPLACE", payPalBaseUrl.toString())


        val context = InstrumentationRegistry.getContext().applicationContext as Application
        val graph = DaggerTestPayPalRedirectSdkComponent.builder()
                .paymentSdkModule(PaymentSdkModule(testPublicKey, backendBaseUrl.toString(), context))
                .oldBsPayoneModule(OldBsPayoneModule(OLD_BS_PAYONE_URL))
                .hyperchargeModule(HyperchargeModule())
                .bsPayoneModule(BsPayoneModule(NEW_BS_PAYONE_URL))
                .build()
        graph.injectTest(this)
        NewPaymentSdk.supplyTestComponent(graph)
        //Traceur.enableLogging()
        Timber.plant(Timber.DebugTree())
    }

    @Test
    fun testPayPalRedirect() {

        val latch = CountDownLatch(1)

        backendMockWebServer.enqueue(MockResponse().setBody(payPalPaymentResponse))
        backendMockWebServer.enqueue(MockResponse().setResponseCode(200))

        val paypalMockPage = MockResponse()
        paypalMockPage.setBody("<html> <body> MOCK PAYPAL </body> </html>")
        paypalMockPage.setHeader("Location", "http://pd.mblb.net/api/v1/success/123456789")
        paypalMockPage.setResponseCode(302)
        payPalMockWebServer.enqueue(paypalMockPage)
        paymentManager.executePayPalPayment(paymentData, billingData).subscribeBy(
                onSuccess = {
                    Timber.d("Got transaction id $it")
                    Assert.assertEquals("123", it)
                    latch.countDown()
                },
                onError = {
                    Timber.e("Failed")
                    latch.countDown()
                }
        )
        latch.await()
    }
}


@Singleton
@Component(modules = [SslSupportModule::class, PaymentSdkModule::class, OldBsPayoneModule::class, HyperchargeModule::class, BsPayoneModule::class])
internal interface TestPayPalRedirectSdkComponent : PaymentSdkComponent {
    fun injectTest(test: PayPalRedirectFlowTest)
}