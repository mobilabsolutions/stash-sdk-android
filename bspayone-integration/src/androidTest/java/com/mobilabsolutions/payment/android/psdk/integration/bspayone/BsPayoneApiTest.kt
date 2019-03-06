package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import android.app.Application
import androidx.test.InstrumentationRegistry
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.internal.*
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.PaymentData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import dagger.Component
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import javax.inject.Singleton


class BsPayoneRegistrationInstrumentationTest {
    val testPublicKey = BuildConfig.newBsTestKey
    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl
    val OLD_BS_PAYONE_URL: String = BuildConfig.oldBsApiUrl
    val NEW_BS_PAYONE_URL = BuildConfig.newBsApiUrl
    val ccAlias = "ybDqWplVEqbnoAARpmcIXvwluuSEbLVN"

    @Inject
    lateinit var registrationManager: NewRegistrationManager


    @Inject
    lateinit var paymentManager: NewPaymentManager

    private var validSepaData: SepaData = SepaData(
            bic = "PBNKDEFF",
            iban = "DE63123456791212121212",
            holder = "Holder Holderman"
    )

    val validCreditCardData = CreditCardData(
            "4111111111111111",
            LocalDate.of(2021, 11, 1),
            "123",
            "Holder Holderman"
    )

    private var paymentData: PaymentData = PaymentData(
            amount = 100,
            currency = "EUR",
            customerId = "1",
            reason = "Test payment"
    )

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getContext().applicationContext as Application

        val integration = BsPayoneIntegration.create()

        val graph = DaggerBsPayoneTestPaymentSdkComponent.builder()
                .sslSupportModule(SslSupportModule(null, null))
                .paymentSdkModule(PaymentSdkModule(testPublicKey, MOBILAB_BE_URL, context, listOf(integration)))
                .bsPayoneModule(BsPayoneModule(NEW_BS_PAYONE_URL))
                .build()

        integration.initialize(graph)

        graph.injectTest(this)
        ////Traceur.enableLogging()
    }

    @Test
    fun registerCreditCard() {

        val latch = CountDownLatch(1)

        val registrationDisposable = registrationManager.registerCreditCard(
                validCreditCardData
        )
                .subscribeOn(Schedulers.io())
                .subscribe { paymentAlias ->
                    Assert.assertNotNull(paymentAlias)
                    println("Payment aliasId: $paymentAlias")
                    latch.countDown()

                }
        try {
            latch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        registrationDisposable.dispose()

    }

//    @Test
//    fun testBSCreditCardPayment() {
//        val latch = CountDownLatch(1)
//
//        val paymentDisposable = paymentManager.executeCreditCardPaymentWithAlias(
//                ccAlias,
//                paymentData
//        )
//                .subscribeOn(Schedulers.io())
//                .subscribe(
//                        { transactionId ->
//                            Assert.assertNotNull(transactionId)
//                            println("Transaction: $transactionId")
//                            latch.countDown()
//
//                        }
//                ) { error ->
//                    Timber.e(error, "BS credit card payment failed")
//                    Assert.fail(error.message)
//                }
//        try {
//            latch.await()
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//
//        paymentDisposable.dispose()
//    }


    @Test
    fun testBSSepaRegistration() {
        val latch = CountDownLatch(1)

        val registrationDisposable = registrationManager.registerSepa(
                validSepaData)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { paymentAlias ->
                            Assert.assertNotNull(paymentAlias)
                            println("Payment aliasId: $paymentAlias")
                            latch.countDown()

                        }

                ) { error ->

                    Assert.fail(error.message)

                    latch.countDown()
                }

        try {
            latch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        registrationDisposable.dispose()
    }

    @Test
    fun registerCreditCardFailure() {

        val latch = CountDownLatch(1)

        val validCreditCardData = CreditCardData(
                "4111111111111111",
                LocalDate.of(2021, 1, 1),
                "123",
                "Holder Holderman"
        )

        registrationManager.registerCreditCard(validCreditCardData).subscribeBy(
                onSuccess = { alias ->
                    System.out.print("Test")
                    latch.countDown()
                },
                onError = {
                    latch.countDown()
                    it.printStackTrace()
                }
        )

        latch.await()

    }
}

@Singleton
@Component(modules = [SslSupportModule::class, PaymentSdkModule::class, BsPayoneModule::class])
internal interface BsPayoneTestPaymentSdkComponent : PaymentSdkComponent {
    fun injectTest(test: BsPayoneRegistrationInstrumentationTest)
}
