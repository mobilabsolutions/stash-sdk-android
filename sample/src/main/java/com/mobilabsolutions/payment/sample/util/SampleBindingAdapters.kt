package com.mobilabsolutions.payment.sample.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mobilabsolutions.payment.sample.R
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.data.entities.PaymentType
import com.mobilabsolutions.payment.sample.data.entities.Product
import com.mobilabsolutions.payment.sample.data.entities.ProductType.*
import com.mobilabsolutions.payment.sample.extensions.priceWithCurrencyString

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 15-04-2019.
 */
@BindingAdapter("priceWithCurrency")
fun priceWithCurrency(textView: TextView, price: Int) {
    textView.text = priceWithCurrencyString(price)
}

@BindingAdapter("imageByProductType")
fun imageByProductType(imageView: ImageView, product: Product) {
    val resId = when (product.productType) {
        MOBILAB_T_SHIRT -> R.drawable.tshirt
        NOTEBOOK_PAPER -> R.drawable.note
        MOBILAB_STICKER -> R.drawable.sticker
        MOBILAB_PEN -> R.drawable.pen
        else -> R.drawable.tshirt
    }
    imageView.setImageResource(resId)
}

@BindingAdapter("paymentImageByType")
fun paymentImageByType(imageView: ImageView, paymentMethod: PaymentMethod) {
    val resId = when (paymentMethod.type) {
        PaymentType.CREDIT_CARD -> R.drawable.credit_card
        PaymentType.SEPA -> R.drawable.sepa
        PaymentType.PAYPAL -> R.drawable.paypal
    }
    imageView.setImageResource(resId)
}