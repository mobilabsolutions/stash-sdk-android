package com.mobilabsolutions.payment.sample.util

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mobilabsolutions.payment.sample.R
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.data.entities.PaymentType
import com.mobilabsolutions.payment.sample.data.entities.Product
import com.mobilabsolutions.payment.sample.data.entities.ProductType.MOBILAB_PEN
import com.mobilabsolutions.payment.sample.data.entities.ProductType.MOBILAB_STICKER
import com.mobilabsolutions.payment.sample.data.entities.ProductType.MOBILAB_T_SHIRT
import com.mobilabsolutions.payment.sample.data.entities.ProductType.NOTEBOOK_PAPER
import com.mobilabsolutions.payment.sample.extensions.priceWithCurrencyString

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 15-04-2019.
 */
@BindingAdapter("priceWithCurrency")
fun priceWithCurrency(textView: TextView, price: Int) {
    textView.text = priceWithCurrencyString(price)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("priceWithCurrencyAndLabel")
fun priceWithCurrencyAndLabel(textView: TextView, price: Int) {
    textView.text = "PAY ${priceWithCurrencyString(price)}"
}

@BindingAdapter("imageByProductType")
fun imageByProductType(imageView: ImageView, product: Product) {
    val resId = when (product.productType) {
        MOBILAB_T_SHIRT -> R.drawable.image_card_01
        NOTEBOOK_PAPER -> R.drawable.image_card_02
        MOBILAB_STICKER -> R.drawable.image_card_03
        MOBILAB_PEN -> R.drawable.image_card_04
        else -> R.drawable.image_card_05
    }
    imageView.setImageResource(resId)
}

@BindingAdapter("paymentImageByType")
fun paymentImageByType(imageView: ImageView, paymentMethod: PaymentMethod) {
    val resId = when (paymentMethod.type) {
        PaymentType.CC -> R.drawable.credit_card
        PaymentType.SEPA -> R.drawable.sepa
        PaymentType.PAY_PAL -> R.drawable.paypal
        else -> R.drawable.credit_card
    }
    imageView.setImageResource(resId)
}