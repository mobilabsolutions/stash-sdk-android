package com.mobilabsolutions.payment.sample.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mobilabsolutions.payment.sample.extensions.priceWithCurrencyString

object BindingAdapters {
    //private val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY)

    @JvmStatic
    @BindingAdapter("currency")
    fun TextView.bindCurrency(price: Int) {
        //text = format.format(price / 100)
        text = priceWithCurrencyString(price)
    }

    @JvmStatic
    @BindingAdapter("image")
    fun ImageView.setImage(id: Int) {
        setImageResource(id)
    }

}