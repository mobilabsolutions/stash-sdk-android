package com.mobilabsolutions.payment.android.psdk.integration.adyen

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.exceptions.base.ValidationException
import com.mobilabsolutions.payment.android.psdk.internal.NewRegistrationManager
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkModule
import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 30-05-2019.
 */
@RunWith(RobolectricTestRunner::class)
class AdyenIntegrationTest {

    val testPublishableKey = BuildConfig.newBsTestKey
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

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        val graph = DaggerAdyenIntegrationTestComponent.builder()
            .sslSupportModule(SslSupportModule(null, null))
            .paymentSdkModule(PaymentSdkModule(testPublishableKey, MOBILAB_BE_URL, context, mapOf(integration to methods), true))
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

        ShadowLog.stream = System.out
    }

    @Test
    fun testVisaRegistration() {
        registrationManager.registerCreditCard(validVisaCreditCardData)
            .subscribeBy(
                onSuccess = {
                    println("Got result ${it.alias}")
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
        registrationManager.registerCreditCard(invalidVisaCreditCardData).subscribeBy(
            onSuccess = {
                Assert.fail("Expected validation throwable")
            },
            onError = {
                Timber.e(it, "Error")
                Assert.assertTrue(it is ValidationException)
            }
        )
    }

    @Test
    fun testMastercardRegistration() {
        registrationManager.registerCreditCard(validMastercardCreditCardData).subscribeBy(
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
    fun testAmexRegistration() {
        registrationManager.registerCreditCard(validAmexCreditCardData).subscribeBy(
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
    fun testSepaRegistration() {
        registrationManager.registerSepaAccount(validSepaData)
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
}