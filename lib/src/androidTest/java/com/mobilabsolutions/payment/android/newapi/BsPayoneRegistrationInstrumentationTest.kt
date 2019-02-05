package com.mobilabsolutions.payment.android.newapi

import android.app.Application
import androidx.test.InstrumentationRegistry
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.internal.NewRegistrationManager
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkModule
import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.bspayone.BsPayoneModule
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.hypercharge.HyperchargeModule
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.oldbspayone.OldBsPayoneModule
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import dagger.Component
import io.reactivex.rxkotlin.subscribeBy
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>

 */


class BsPayoneRegistrationInstrumentationTest {
    val testPublicKey = BuildConfig.newBsTestKey
    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl
    val OLD_BS_PAYONE_URL: String = BuildConfig.oldBsApiUrl
    val NEW_BS_PAYONE_URL = BuildConfig.newBsApiUrl

    @Inject
    lateinit var registrationManager: NewRegistrationManager

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getContext().applicationContext as Application
        val graph = DaggerTestPaymentSdkComponent.builder()
                .paymentSdkModule(PaymentSdkModule(testPublicKey, MOBILAB_BE_URL, context))
                .oldBsPayoneModule(OldBsPayoneModule(OLD_BS_PAYONE_URL))
                .hyperchargeModule(HyperchargeModule())
                .bsPayoneModule(BsPayoneModule(NEW_BS_PAYONE_URL))
                .build()
        graph.injectTest(this)
        ////Traceur.enableLogging()
    }

    @Test
    fun registerCreditCard() {

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
@Component(modules = [SslSupportModule::class, PaymentSdkModule::class, OldBsPayoneModule::class, HyperchargeModule::class, BsPayoneModule::class])
internal interface TestPaymentSdkComponent : PaymentSdkComponent {
    fun injectTest(test: BsPayoneRegistrationInstrumentationTest)
}