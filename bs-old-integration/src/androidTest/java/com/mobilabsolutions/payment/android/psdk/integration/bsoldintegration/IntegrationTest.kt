package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration

import android.app.Application
import android.os.Build
import androidx.test.InstrumentationRegistry
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.internal.*
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.hypercharge.HyperchargeModule
import dagger.Component
import org.junit.Before
import org.junit.Test
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class IntegrationTest {

    val testPublicKey: String = BuildConfig.oldBsTestKey
    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl
    val OLD_BS_PAYONE_URL: String = BuildConfig.oldBsApiUrl
    val NEW_BS_PAYONE_URL: String = BuildConfig.newBsApiUrl

    @Before
    fun setUp() {

    }


    @Test
    fun testIntegration() {
        val context = InstrumentationRegistry.getContext().applicationContext as Application

        val integrationInitialization = BsOldIntegration.create()


        val graphBuilder = DaggerTestOldBsIntegrationSdkComponent.builder()
                .paymentSdkModule(PaymentSdkModule(testPublicKey, MOBILAB_BE_URL, context, listOf(integrationInitialization)))
                .hyperchargeModule(HyperchargeModule())
                .bsPayoneModule(BsPayoneModule(NEW_BS_PAYONE_URL))

        if (Build.VERSION.SDK_INT < 20) {
//            graphBuilder.sslSupportModule(SslSupportModule(TLSSocketFactoryCompat(), SupportX509TrustManager.getTrustManager()))
        } else {
            graphBuilder.sslSupportModule(SslSupportModule())
        }
        val graph = graphBuilder.build()


        integrationInitialization.initialize(graph)

    }
}

@Singleton
@Component(modules = [SslSupportModule::class, PaymentSdkModule::class, HyperchargeModule::class, BsPayoneModule::class])
internal interface TestOldBsIntegrationSdkComponent : PaymentSdkComponent {

}
