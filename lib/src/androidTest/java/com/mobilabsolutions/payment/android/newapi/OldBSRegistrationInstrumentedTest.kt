package com.mobilabsolutions.payment.android.newapi

import android.app.Application
import android.os.Build
import androidx.test.InstrumentationRegistry
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.internal.*
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.hypercharge.HyperchargeModule
import com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.oldbspayone.OldBsPayoneModule
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.PaymentData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
//import com.tspoon.traceur.Traceur
import dagger.Component
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import javax.inject.Singleton


/**
 * @author [Ugi](ugi@mobilabsolutions.com)
 */
class OldBSRegistrationInstrumentedTest {

    val testPublicKey: String = BuildConfig.oldBsTestKey
    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl
    val OLD_BS_PAYONE_URL: String = BuildConfig.oldBsApiUrl
    val NEW_BS_PAYONE_URL: String = BuildConfig.newBsApiUrl

    private var validCreditCardData: CreditCardData = CreditCardData(
            "4111111111111111",
            LocalDate.of(2021, 1, 1),
            "123",
            "Holder Holderman"
    )
    private var validSepaData: SepaData = SepaData(
            bic = "PBNKDEFF",
            iban = "DE63123456791212121212",
            holder = "Holder Holderman"
    )
    private var validBillingData: BillingData = BillingData(
            city = "Cologne",
            email = "holder@email.test",
            address1 = "Street 1",
            country = "Germany",
            firstName = "Holder",
            lastName = "Holderman",
            zip = "12345"
    )
    private var paymentData: PaymentData = PaymentData(
            amount = 100,
            currency = "EUR",
            customerId = "1",
            reason = "Test payment"
    )

    @Inject
    lateinit var registrationManager: NewRegistrationManager

    @Inject
    lateinit var paymentManager: NewPaymentManager

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getContext().applicationContext as Application
        val graphBuilder = DaggerTestOldBsRegistrationSdkComponent.builder()
                .paymentSdkModule(PaymentSdkModule(testPublicKey, MOBILAB_BE_URL, context))
                .oldBsPayoneModule(com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.oldbspayone.OldBsPayoneModule(OLD_BS_PAYONE_URL))
                .hyperchargeModule(HyperchargeModule())
                .bsPayoneModule(BsPayoneModule(NEW_BS_PAYONE_URL))

        if (Build.VERSION.SDK_INT < 20) {
            graphBuilder.sslSupportModule(SslSupportModule(TLSSocketFactoryCompat(), SupportX509TrustManager.getTrustManager()))
        } else {
            graphBuilder.sslSupportModule(SslSupportModule())
        }
        val graph = graphBuilder.build()
        graph.injectTest(this)
        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(InstrumentationRegistry.getContext())
    }

    @Test
    fun testBSCardRegistration() {

        Timber.d("Starting test card registration")

        val latch = CountDownLatch(1)

        val registrationDisposable = registrationManager.registerCreditCard(
                validCreditCardData
        )
                .subscribeOn(Schedulers.io())
                .subscribe { paymentAlias ->
                    Assert.assertNotNull(paymentAlias)
                    println("Payment alias: $paymentAlias")
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
    fun testBSSepaRegistration() {
        val latch = CountDownLatch(1)

        val registrationDisposable = registrationManager.registerSepa(
                validSepaData)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { paymentAlias ->
                            Assert.assertNotNull(paymentAlias)
                            println("Payment alias: $paymentAlias")
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

//    @Test
//    fun testCreditCardRemoval() {
//        Timber.d("Starting remove card alias test")
//
//        val latch = CountDownLatch(1)
//
//        val registrationDisposable = registrationManager.registerCreditCard(
//                validCreditCardData
//        )
//                .subscribeOn(Schedulers.io())
//                .flatMap { alias ->
//                    Timber.d("Got alias: $alias")
//                    registrationManager.removeCreditCardAlias(alias).andThen(Single.just(alias))
//                }.flatMap { alias -> paymentManager.executeCreditCardPaymentWithAlias(alias, paymentData) }
//                .subscribeBy(
//                        onSuccess = { transactionId ->
//                            Timber.d("Got transaction id after alias deletion!")
//                            latch.countDown()
//                        },
//                        onError = { error ->
//                            Timber.d("Removing alias reported an error")
//
//                            Assert.assertTrue(error is UnknownBackendException)
//                            Assert.assertEquals(error.message, "Payment method is inactive")
////                            if (error is HttpException) {
////                                if (error.code() != 400) {
////                                    Assert.fail(error.message)
////                                    Timber.e(error, "Removing alias reported an error")
////                                }
////                            } else {
////                                Assert.fail(error.message)
////                                Timber.e(error, "Removing alias reported an error")
////                            }
//
//                            latch.countDown()
//                        }
//
//                )
//        try {
//            latch.await()
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//
//        registrationDisposable.dispose()
//    }

//    @Test
//    fun testSepaRemoval() {
//        Timber.d("Starting remove card alias test")
//
//        val latch = CountDownLatch(1)
//
//        val registrationDisposable = registrationManager.registerSepa(
//                validSepaData
//        )
//                .subscribeOn(Schedulers.io())
//                .flatMap { alias ->
//                    Timber.d("Got alias: $alias")
//                    registrationManager.removeSepaAlias(alias).andThen(Single.just(alias))
//                }.flatMap { alias -> paymentManager.executeCreditCardPaymentWithAlias(alias, paymentData) }
//                .subscribeBy(
//                        onSuccess = { transactionId ->
//                            Timber.d("Got transaction id after alias deletion!")
//                            latch.countDown()
//                        },
//                        onError = { error ->
//                            Timber.d("Removing alias reported an error")
//
//                            Assert.assertTrue(error is UnknownBackendException)
//                            Assert.assertEquals(error.message, "Payment method is inactive")
//
//                            latch.countDown()
//                        }
//                )
//        try {
//            latch.await()
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//
//        registrationDisposable.dispose()
//    }


}

@Singleton
@Component(modules = [SslSupportModule::class, PaymentSdkModule::class, com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.oldbspayone.OldBsPayoneModule::class, HyperchargeModule::class, BsPayoneModule::class])
internal interface TestOldBsRegistrationSdkComponent : PaymentSdkComponent {
    fun injectTest(test: OldBSRegistrationInstrumentedTest)
}


