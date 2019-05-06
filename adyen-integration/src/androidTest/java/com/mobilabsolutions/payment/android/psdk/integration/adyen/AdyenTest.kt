package com.mobilabsolutions.payment.android.psdk.integration.adyen

import android.app.Application
import androidx.test.core.app.ApplicationProvider
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
import junit.framework.Assert.assertTrue
import org.junit.Assert
import org.threeten.bp.LocalDate
import timber.log.Timber
import java.util.concurrent.CountDownLatch

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class AdyenTest {

    val testPublicKey = BuildConfig.newBsTestKey
    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl

    @Inject
    lateinit var registrationManager: NewRegistrationManager

    lateinit var validVisaCreditCardData: CreditCardData
    lateinit var validMastercardCreditCardData: CreditCardData
    lateinit var validAmexCreditCardData: CreditCardData


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

        validVisaCreditCardData = CreditCardData(
                number = "4111111111111111",
                expiryDate = LocalDate.of(2020, 10, 1),
                cvv = "737",
                holder = "Holder Holdermann"
        )

        validMastercardCreditCardData = CreditCardData(
                number = "5555 3412 4444 1115",
                expiryDate = LocalDate.of(2020, 10, 1),
                cvv = "737",
                holder = "Holder Holdermann"
        )

        validAmexCreditCardData = CreditCardData(
                number = "370000000000002",
                expiryDate = LocalDate.of(2020, 10, 1),
                cvv = "7373",
                holder = "Holder Holdermann"
        )
    }

    @Test
    fun testVisaRegistration() {
        setUp()
        val latch = CountDownLatch(1)
        registrationManager.registerCreditCard(validVisaCreditCardData).subscribeBy(
                onSuccess = {
                    assertTrue(it.alias.isNotEmpty())
                    latch.countDown()
                },
                onError = {
                    Timber.e(it, "Error")
                    Assert.fail(it.message)
                    latch.countDown()
                }
        )
        latch.await()
    }

    @Test
    fun testMastercardRegistration() {
        setUp()
        val latch = CountDownLatch(1)
        registrationManager.registerCreditCard(validMastercardCreditCardData).subscribeBy(
                onSuccess = {
                    assertTrue(it.alias.isNotEmpty())
                    latch.countDown()
                },
                onError = {
                    Timber.e(it, "Error")
                    Assert.fail(it.message)
                    latch.countDown()
                }
        )
        latch.await()
    }

    @Test
    fun testAmexRegistration() {
        setUp()
        val latch = CountDownLatch(1)
        registrationManager.registerCreditCard(validAmexCreditCardData).subscribeBy(
                onSuccess = {
                    assertTrue(it.alias.isNotEmpty())
                    latch.countDown()
                },
                onError = {
                    Timber.e(it, "Error")
                    Assert.fail(it.message)
                    latch.countDown()
                }
        )
        latch.await()
    }
    
}

@Singleton
@Component(modules = [SslSupportModule::class, PaymentSdkModule::class, AdyenModule::class])
internal interface AdyenTestPaymentSdkComponent : PaymentSdkComponent {
    fun injectTest(test: AdyenTest)
}
