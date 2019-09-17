package com.mobilabsolutions.stash.core.ui.picker

import android.content.Context
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.PaymentMethodType.CC
import com.mobilabsolutions.stash.core.PaymentMethodType.PAYPAL
import com.mobilabsolutions.stash.core.PaymentMethodType.SEPA
import com.mobilabsolutions.stash.core.R
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
class PaymentPickerTextCreator @Inject constructor(
    private val context: Context
) {
    fun paymentTypeText(paymentMethodType: PaymentMethodType): CharSequence {
        return when (paymentMethodType) {
            CC -> context.getString(R.string.payment_chooser_credit_card)
            SEPA -> context.getString(R.string.payment_chooser_sepa)
            PAYPAL -> context.getString(R.string.payment_chooser_paypal)
        }
    }
}