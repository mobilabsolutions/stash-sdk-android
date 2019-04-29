package com.mobilabsolutions.payment.android.psdk.integration.adyen

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobilabsolutions.payment.android.psdk.internal.NewRegistrationManager
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkModule
import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import dagger.Component
import org.junit.Test
import javax.inject.Inject
import javax.inject.Singleton

import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import io.reactivex.rxkotlin.subscribeBy
import org.junit.Assert
import org.junit.Before
import org.threeten.bp.LocalDate
import timber.log.Timber
import java.util.concurrent.CountDownLatch

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class AdyenTest {

    val testPublicKey = BuildConfig.newBsTestKey
    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl
    val OLD_BS_PAYONE_URL: String = BuildConfig.oldBsApiUrl
    val NEW_BS_PAYONE_URL = BuildConfig.newBsApiUrl
    val ccAlias = "ybDqWplVEqbnoAARpmcIXvwluuSEbLVN"

    @Inject
    lateinit var registrationManager: NewRegistrationManager

    lateinit var validCreditCardData : CreditCardData

    fun setUp() {
        val context = ApplicationProvider.getApplicationContext() as Application

        val integration = AdyenIntegration.create()

        val graph = DaggerAdyenTestPaymentSdkComponent.builder()
                .sslSupportModule(SslSupportModule(null, null))
                .paymentSdkModule(PaymentSdkModule(testPublicKey, MOBILAB_BE_URL, context, listOf(integration), true))
                .adyenModule(AdyenModule())
                .build()

        integration.initialize(graph)

        AndroidThreeTen.init(context)

        graph.injectTest(this)

        validCreditCardData = CreditCardData(
                number = "4111111111111111",
                expiryDate = LocalDate.now(),
                cvv = "123",
                holder = "Holder"
        )
    }


    @Test
    fun retrieveToken() {
        setUp()
        val latch = CountDownLatch(1)
        registrationManager.registerCreditCard(validCreditCardData).subscribeBy(
                onSuccess = {
                    Timber.d("Alias ${it.alias}")
                    latch.countDown()
                },
                onError = {
                    Timber.e(it, "Error")
                    Assert.fail()
                    latch.countDown()
                }
        )
        latch.await()
    }

//    @Ignore("Idempotent-Key needs to be implemented.")
//    @Test
//    fun testRegisterCreditCard() {
//        setUp()
//
//        val latch = CountDownLatch(1)
//
//        val registrationDisposable = registrationManager.registerCreditCard(
//                validCreditCardData
//        )
//                .subscribeOn(Schedulers.io())
//                .subscribeBy(
//                        onSuccess = { paymentAlias ->
//                            Assert.assertNotNull(paymentAlias)
//                            println("Payment aliasId: $paymentAlias")
//                            latch.countDown()
//                        },
//                        onError = {
//                            Timber.e(it, "Failed")
//                            fail(it.message)
//                            latch.countDown()
//                        }
//                )
//
//        latch.await()
//
//        registrationDisposable.dispose()
//    }
//
//    @Ignore("Not implemented on backend yet")
//    @Test
//    fun testBSSepaRegistration() {
//        val latch = CountDownLatch(1)
//
//        val registrationDisposable = registrationManager.registerSepa(
//                validSepaData, validBillingData)
//                .subscribeOn(Schedulers.io())
//                .subscribe(
//                        { paymentAlias ->
//                            Assert.assertNotNull(paymentAlias)
//                            println("Payment aliasId: $paymentAlias")
//                            latch.countDown()
//                        }
//
//                ) { error ->
//
//                    Assert.fail(error.message)
//
//                    latch.countDown()
//                }
//
//        try {
//            latch.await()
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//
//        registrationDisposable.dispose()
//    }
//    @Ignore("Not implemented on backend yet")
//    @Test
//    fun registerCreditCardFailure() {
//
//        val latch = CountDownLatch(1)
//
//        val validCreditCardData = CreditCardData(
//                "4111111111111111",
//                LocalDate.of(2021, 1, 1),
//                "123",
//                "Holder Holderman"
//        )
//
//        registrationManager.registerCreditCard(validCreditCardData).subscribeBy(
//                onSuccess = { alias ->
//                    System.out.print("Test")
//                    latch.countDown()
//                },
//                onError = {
//                    latch.countDown()
//                    it.printStackTrace()
//                }
//        )
//
//        latch.await()
//    }
}





@Singleton
@Component(modules = [SslSupportModule::class, PaymentSdkModule::class, AdyenModule::class])
internal interface AdyenTestPaymentSdkComponent : PaymentSdkComponent {
    fun injectTest(test: AdyenTest)
}
