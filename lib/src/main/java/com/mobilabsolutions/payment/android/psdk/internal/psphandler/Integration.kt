package com.mobilabsolutions.payment.android.psdk.internal.psphandler

import android.content.Context
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import io.reactivex.Single

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface Integration {

    fun initialize(appDaggerGraph : PaymentSdkComponent)

    fun handleRegistrationRequest(registrationRequest: RegistrationRequest) : Single<String>

}