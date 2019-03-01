package com.mobilabsolutions.payment.android.psdk.integration.stripe

import android.content.Context
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.R
import com.stripe.android.Stripe

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
object StripeInitializer {

    fun initialize(context: Context): Stripe {
        return Stripe(context, context.getString(R.string.stripe_public_key)
                ?: throw RuntimeException("Missing Stripe publishable key"))
    }
}