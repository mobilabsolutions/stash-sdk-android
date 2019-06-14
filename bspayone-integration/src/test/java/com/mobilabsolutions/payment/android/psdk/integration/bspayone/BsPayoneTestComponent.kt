package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkModule
import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [SslSupportModule::class, PaymentSdkModule::class, BsPayoneModule::class])
interface BsPayoneTestComponent : PaymentSdkComponent {
    fun injectTest(test: BsPayoneIntegrationTest)
}