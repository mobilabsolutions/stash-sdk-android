package com.mobilabsolutions.payment.android.psdk.internal

import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApi
import com.mobilabsolutions.payment.android.psdk.internal.api.oldbspayone.OldBsPayoneApi
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.oldbspayone.OldBsPayoneModule
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.hypercharge.HyperchargeModule
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.bspayone.BsPayoneModule
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.psppaypal.PayPalRedirectActivity
import dagger.Component
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Singleton
@Component(modules = arrayOf(SslSupportModule::class, PaymentSdkModule::class, OldBsPayoneModule::class, HyperchargeModule::class, BsPayoneModule::class))
interface PaymentSdkComponent {
    fun inject(paymentSdk: NewPaymentSdk)

    fun inject(payPalRedirectActivity: PayPalRedirectActivity)

    fun provideOldBsApi() : OldBsPayoneApi

    fun provideMobilabApi() : MobilabApi
}