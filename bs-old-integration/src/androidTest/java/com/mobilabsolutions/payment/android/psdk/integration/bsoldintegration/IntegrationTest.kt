package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration

import android.app.Application
import android.os.Build
import androidx.test.InstrumentationRegistry
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.internal.SupportX509TrustManager
import com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.internal.TLSSocketFactoryCompat
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkModule
import com.mobilabsolutions.payment.android.psdk.internal.PspCoordinator
import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.PaymentData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
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
class IntegrationTest {

    val testPublicKey: String = BuildConfig.oldBsTestKey
    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl
    val OLD_BS_PAYONE_URL: String = BuildConfig.oldBsApiUrl
    val NEW_BS_PAYONE_URL: String = BuildConfig.newBsApiUrl

    @Inject
    lateinit var pspCoordinator : PspCoordinator

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

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getContext().applicationContext as Application
        val integrationInitialization = BsOldIntegration.create()

        val graphBuilder = DaggerTestOldBsIntegrationSdkComponent.builder()
                .paymentSdkModule(PaymentSdkModule(testPublicKey, "https://pd.mblb.net/api/", context, listOf(integrationInitialization)))

        if (Build.VERSION.SDK_INT < 20) {
            graphBuilder.sslSupportModule(SslSupportModule(TLSSocketFactoryCompat(), SupportX509TrustManager.getTrustManager()))
        } else {
            graphBuilder.sslSupportModule(SslSupportModule())
        }
        val graph = graphBuilder.build()


        integrationInitialization.initialize(graph)
        graph.inject(this)
    }



    @Test
    fun testCardRegistration() {
        val latch = CountDownLatch(1)
        pspCoordinator.handleRegisterCreditCardOld(validCreditCardData).subscribeBy(
                onSuccess = {
                    assert(it.isNotEmpty()) { "Empty alias" }
                    latch.countDown()
                }
        )
        latch.await()
    }

}

@Singleton
@Component(modules = [SslSupportModule::class, PaymentSdkModule::class])
internal interface TestOldBsIntegrationSdkComponent : PaymentSdkComponent {
    fun inject(integrationTest : IntegrationTest)

    fun providePspCoordinator() : PspCoordinator
}
