/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import android.app.Application
import androidx.test.InstrumentationRegistry
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.RegistrationManagerImpl
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkModule
import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import dagger.Component
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Ignore
import org.junit.Test
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import javax.inject.Singleton

class BsPayoneRegistrationInstrumentationTest {
    val testPublishableKey = BuildConfig.testPublishableKey
    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl
    val NEW_BS_PAYONE_URL = BuildConfig.newBsApiUrl
    val ccAlias = "ybDqWplVEqbnoAARpmcIXvwluuSEbLVN"

    @Inject
    lateinit var registrationManager: RegistrationManagerImpl

    private var validBillingData: BillingData = BillingData(
        city = "Cologne",
        email = "holder@email.test",
        address1 = "Street 1",
        country = "Germany",
        firstName = "Holder",
        lastName = "Holderman"
    )

    private var validSepaData: SepaData = SepaData(
        bic = "PBNKDEFF",
        iban = "DE63123456791212121212",
        billingData = validBillingData
    )

    val validCreditCardData = CreditCardData(
        "4111111111111111",
        10,
        2020,
        "123",
        validBillingData
    )

    fun setUp() {
        val context = InstrumentationRegistry.getContext().applicationContext as Application

        val methods = setOf(PaymentMethodType.SEPA, PaymentMethodType.CC)
        val integration = BsPayoneIntegration.create(methods)

        val graph = DaggerBsPayoneTestPaymentSdkComponent.builder()
            .sslSupportModule(SslSupportModule(null, null))
            .paymentSdkModule(PaymentSdkModule(testPublishableKey, MOBILAB_BE_URL, context, mapOf(integration to methods), true))
            .bsPayoneModule(BsPayoneModule(NEW_BS_PAYONE_URL))
            .build()

        integration.initialize(graph)

        graph.injectTest(this)
        // //Traceur.enableLogging()
    }

    @Ignore("Idempotent-Key needs to be implemented.")
    @Test
    fun testRegisterCreditCard() {
        setUp()

        val latch = CountDownLatch(1)

        val registrationDisposable = registrationManager.registerCreditCard(
            validCreditCardData
        )
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = { paymentAlias ->
                    Assert.assertNotNull(paymentAlias)
                    println("Payment aliasId: $paymentAlias")
                    latch.countDown()
                },
                onError = {
                    Timber.e(it, "Failed")
                    fail(it.message)
                    latch.countDown()
                }
            )

        latch.await()

        registrationDisposable.dispose()
    }

    @Ignore("Not implemented on backend yet")
    @Test
    fun testBSSepaRegistration() {
        val latch = CountDownLatch(1)

        val registrationDisposable = registrationManager.registerSepaAccount(
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

    @Ignore("Not implemented on backend yet")
    @Test
    fun registerCreditCardFailure() {

        val latch = CountDownLatch(1)

        val validCreditCardData = CreditCardData(
            "4111111111111111",
            1,
            2021,
            "123",
            validBillingData
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
