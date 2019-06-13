package com.mobilabsolutions.payment.android.psdk.integration.adyen

import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkModule
import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import dagger.Component
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 30-05-2019.
 */
@Singleton
@Component(modules = [SslSupportModule::class, PaymentSdkModule::class, AdyenModule::class])
internal interface AdyenIntegrationTestComponent : PaymentSdkComponent {
    fun injectTest(test: IntegrationTest)
}