package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentManager
import com.mobilabsolutions.payment.android.psdk.internal.NewRegistrationManager
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkModule
import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.PaymentData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import dagger.Component
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.threeten.bp.LocalDate
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@RunWith(RobolectricTestRunner::class)
class RoboTest {
    companion object {
        private val testPublicKey = BuildConfig.newBsTestKey
        private val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl
        private val OLD_BS_PAYONE_URL: String = BuildConfig.oldBsApiUrl
        private val NEW_BS_PAYONE_URL = BuildConfig.newBsApiUrl
        private val ccAlias = "ybDqWplVEqbnoAARpmcIXvwluuSEbLVN"
    }

    @Inject
    lateinit var registrationManager: NewRegistrationManager

    @Inject
    lateinit var paymentManager: NewPaymentManager

    private var validSepaData: SepaData = SepaData(
            bic = "PBNKDEFF",
            iban = "DE63123456791212121212",
            holder = "Holder Holderman"
    )

    private val validCreditCardData = CreditCardData(
            "4111111111111111",
            LocalDate.of(2021, 11, 1),
            "123",
            "Holder Holderman"
    )

    private val paymentData: PaymentData = PaymentData(
            amount = 100,
            currency = "EUR",
            customerId = "1",
            reason = "Test payment"
    )

    private val validBillingData: BillingData = BillingData(
            city = "Cologne",
            email = "holder@email.test",
            address1 = "Street 1",
            country = "Germany",
            firstName = "Holder",
            lastName = "Holderman"
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>() as Application

        val integration = BsPayoneIntegration.create()

        val graph = DaggerRoboTestComponentet.builder()
                .sslSupportModule(SslSupportModule(null, null))
                .paymentSdkModule(PaymentSdkModule(testPublicKey, MOBILAB_BE_URL, context, listOf(integration), true))
                .bsPayoneModule(BsPayoneModule(NEW_BS_PAYONE_URL))
                .build()

        integration.initialize(graph)

        graph.injectTest(this)
        // //Traceur.enableLogging()
    }

    @Test
    fun testRegisterCreditCard() {
        val registrationDisposable = registrationManager.registerCreditCard(
                validCreditCardData
        )
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                        onSuccess = { paymentAlias ->
                            Assert.assertNotNull(paymentAlias)
                            println("Payment aliasId: $paymentAlias")
                        },
                        onError = {
                            Timber.e(it, "Failed")
                            fail(it.message)
                        }
                )

        registrationDisposable.dispose()
    }

    @Test
    fun testBSSepaRegistration() {
        val registrationDisposable = registrationManager.registerSepa(
                validSepaData, validBillingData)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { paymentAlias ->
                            Assert.assertNotNull(paymentAlias)
                            println("Payment aliasId: $paymentAlias")
                        }

                ) { error ->
                    fail(error.message)
                }
        registrationDisposable.dispose()
    }

    @Test
    fun registerCreditCardFailure() {

        val validCreditCardData = CreditCardData(
                "4111111111111111",
                LocalDate.of(2021, 1, 1),
                "123",
                "Holder Holderman"
        )

        registrationManager.registerCreditCard(validCreditCardData).subscribeBy(
                onSuccess = { alias ->
                    System.out.print("Test")
                },
                onError = {
                    it.printStackTrace()
                }
        )
    }
}

@Singleton
@Component(modules = [SslSupportModule::class, PaymentSdkModule::class, BsPayoneModule::class])
internal interface RoboTestComponentet : PaymentSdkComponent {
    fun injectTest(test: RoboTest)
}
