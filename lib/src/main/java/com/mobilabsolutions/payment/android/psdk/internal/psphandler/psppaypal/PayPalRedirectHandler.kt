package com.mobilabsolutions.payment.android.psdk.internal.psphandler.psppaypal

import android.app.Application
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */

class PayPalRedirectHandler @Inject constructor(
    val applicationContext: Application,
    val redirectActivitySingle: Single<RedirectResult>
) {

    enum class RedirectState {
        SUCCESS, FAILURE, CANCELED, UNKNOWN
    }

    data class RedirectResult(val redirectState: RedirectState, val code: String)
}