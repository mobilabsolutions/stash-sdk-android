package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.exceptions.base.ValidationException
import com.mobilabsolutions.payment.android.psdk.integration.adyen.AdyenIntegration
import com.mobilabsolutions.payment.android.psdk.integration.adyen.AdyenModule
import com.mobilabsolutions.payment.android.psdk.internal.NewRegistrationManager
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkModule
import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.rxkotlin.subscribeBy
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 30-05-2019.
 */
@RunWith(RobolectricTestRunner::class)
class IntegrationTest {

    val testPublicKey = BuildConfig.newBsTestKey
    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl

    @Inject
    lateinit var registrationManager: NewRegistrationManager

    lateinit var validVisaCreditCardData: CreditCardData
    lateinit var invalidVisaCreditCardData: CreditCardData
    lateinit var validMastercardCreditCardData: CreditCardData
    lateinit var validAmexCreditCardData: CreditCardData

    lateinit var validSepaData: SepaData

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext() as Application
        val methods = setOf(PaymentMethodType.SEPA, PaymentMethodType.CC)
        val integration = AdyenIntegration.create(methods)

        val graph = DaggerAdyenIntegrationTestComponent.builder()
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
        registrationManager.registerCreditCard(validVisaCreditCardData)
            .subscribeBy(
                onSuccess = {
                    Assert.assertTrue(it.alias.isNotEmpty())
                },
                onError = {
                    Timber.e(it, "Error")
                    Assert.fail(it.message)
                }
            )
    }

    @Test
    fun testVisaRegistrationFailure() {
        val latch = CountDownLatch(1)
        registrationManager.registerCreditCard(invalidVisaCreditCardData).subscribeBy(
            onSuccess = {
                Assert.fail("Expected validation throwable")
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
        val latch = CountDownLatch(1)
        registrationManager.registerCreditCard(validMastercardCreditCardData).subscribeBy(
            onSuccess = {
                Assert.assertTrue(it.alias.isNotEmpty())
                latch.countDown()
            },
            onError = {
                Timber.e(it, "Error")
                Assert.fail(it.message)
                latch.countDown()
            }
        )
    }

    @Test
    fun testAmexRegistration() {

        val latch = CountDownLatch(1)
        registrationManager.registerCreditCard(validAmexCreditCardData).subscribeBy(
            onSuccess = {
                Assert.assertTrue(it.alias.isNotEmpty())
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

        val latch = CountDownLatch(1)
        registrationManager.registerSepaAccount(validSepaData)
            .subscribeBy(
                onSuccess = {
                    Assert.assertTrue(it.alias.isNotEmpty())
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