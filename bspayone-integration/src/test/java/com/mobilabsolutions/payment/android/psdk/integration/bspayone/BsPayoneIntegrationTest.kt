package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.RegistrationManagerImpl
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkModule
import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import timber.log.Timber
import javax.inject.Inject

@RunWith(RobolectricTestRunner::class)
class BsPayoneIntegrationTest {
    private val testPublishableKey = BuildConfig.newBsTestKey
    private val mobilabBeUrl: String = BuildConfig.mobilabBackendUrl
    private val newBsPayoneUrl = BuildConfig.newBsApiUrl

    @Inject
    lateinit var registrationManager: RegistrationManagerImpl

    private val validBillingData: BillingData = BillingData(
        city = "Cologne",
        email = "holder@email.test",
        address1 = "Street 1",
        country = "Germany",
        firstName = "Holder",
        lastName = "Holderman"
    )

    private val validSepaData: SepaData = SepaData(
        bic = "PBNKDEFF",
        iban = "DE63123456791212121212",
        billingData = validBillingData
    )

    private val validCreditCardData = CreditCardData(
        "4111111111111111",
        10,
        2020,
        "123",
        validBillingData
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext() as Application
        val methods = setOf(PaymentMethodType.SEPA, PaymentMethodType.CC)
        val integration = BsPayoneIntegration.create(methods)

        val graph = DaggerBsPayoneTestComponent.builder()
            .sslSupportModule(SslSupportModule(null, null))
            .paymentSdkModule(PaymentSdkModule(testPublishableKey, mobilabBeUrl, context, mapOf(integration to methods), true))
            .bsPayoneModule(BsPayoneModule(newBsPayoneUrl))
            .build()

        integration.initialize(graph)

        graph.injectTest(this)

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        ShadowLog.stream = System.out
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun testRegisterCreditCard() {
        registrationManager.registerCreditCard(validCreditCardData)
            .subscribeBy(
                onSuccess = { paymentAlias ->
                    Assert.assertNotNull(paymentAlias)
                    println("Payment aliasId: $paymentAlias")
                },
                onError = {
                    Timber.e(it, "Failed")
                    Assert.fail(it.message)
                }
            )
    }

    @Test
    fun testBSSepaRegistration() {
        registrationManager.registerSepaAccount(
            validSepaData)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { paymentAlias ->
                    Assert.assertNotNull(paymentAlias)
                    println("Payment aliasId: $paymentAlias")
                }

            ) { error ->

                Assert.fail(error.message)
            }
    }

    @Test
    fun registerCreditCardFailure() {
        val invalidCreditCardData = CreditCardData(
            "4111111111111112",
            1,
            2021,
            "123",
            validBillingData
        )

        registrationManager.registerCreditCard(invalidCreditCardData).subscribeBy(
            onSuccess = {
                System.out.print("Test")
            },
            onError = {
                Assert.fail(it.message)
            }
        )
    }
}