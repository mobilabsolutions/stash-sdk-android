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
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.exceptions.base.ValidationException
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.rxkotlin.subscribeBy
import junit.framework.Assert.assertTrue
import org.junit.Assert
import org.junit.Ignore
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
    lateinit var invalidVisaCreditCardData: CreditCardData
    lateinit var validMastercardCreditCardData: CreditCardData
    lateinit var validAmexCreditCardData: CreditCardData

    lateinit var validSepaData: SepaData

    fun setUp() {
        val context = ApplicationProvider.getApplicationContext() as Application
        val methods = setOf(PaymentMethodType.SEPA, PaymentMethodType.CC)
        val integration = AdyenIntegration.create(methods)

        val graph = DaggerAdyenTestPaymentSdkComponent.builder()
            .sslSupportModule(SslSupportModule(null, null))
            .paymentSdkModule(PaymentSdkModule(testPublicKey, MOBILAB_BE_URL, context, mapOf(integration to methods), true))
            .adyenModule(AdyenModule())
            .build()

        integration.initialize(graph)

        AndroidThreeTen.init(context)

        graph.injectTest(this)

        validVisaCreditCardData = CreditCardData(
            number = "4111111111111111",
            expiryMonth = 10,
            expiryYear = 2020,
            cvv = "737",
            billingData = BillingData("Holder", "Holdermann")
        )

        invalidVisaCreditCardData = CreditCardData(
            number = "4111111111111111",
            expiryMonth = 10,
            expiryYear = 2020,
            cvv = "7372",
            billingData = BillingData("Holder", "Holdermann")
        )

        validMastercardCreditCardData = CreditCardData(
            number = "5555 3412 4444 1115",
            expiryMonth = 10,
            expiryYear = 2020,
            cvv = "737",
            billingData = BillingData("Holder", "Holdermann")
        )

        validAmexCreditCardData = CreditCardData(
            number = "370000000000002",
            expiryMonth = 10,
            expiryYear = 2020,
            cvv = "7373",
            billingData = BillingData("Holder", "Holdermann")
        )

        validSepaData = SepaData(
            billingData = BillingData("Holder", "Holdermann"),
            iban = "DE63123456791212121212"
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
    fun testVisaRegistrationFailure() {
        setUp()
        val latch = CountDownLatch(1)
        registrationManager.registerCreditCard(invalidVisaCreditCardData).subscribeBy(
            onSuccess = {
                Assert.fail("Expected validation exception")
                latch.countDown()
            },
            onError = {
                Timber.e(it, "Error")
                Assert.assertTrue(it is ValidationException)
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

    @Ignore("While backend catches up")
    @Test
    fun testSepaRegistration() {
        setUp()
        val latch = CountDownLatch(1)
        registrationManager.registerSepaAccount(validSepaData)
            .subscribeBy(
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
