package com.mobilabsolutions.stash.core.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.R

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
@BindingAdapter("iconByPaymentMethodType")
fun iconByPaymentMethodType(imageView: ImageView, paymentMethodType: PaymentMethodType) {
    val resId = when (paymentMethodType) {
        PaymentMethodType.CC -> R.drawable.ic_credit_card
        PaymentMethodType.SEPA -> R.drawable.ic_sepa
        PaymentMethodType.PAYPAL -> R.drawable.ic_paypal
    }
    imageView.setImageResource(resId)
}
