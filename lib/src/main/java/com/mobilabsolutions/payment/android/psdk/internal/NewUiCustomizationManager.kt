package com.mobilabsolutions.payment.android.psdk.internal

import android.content.SharedPreferences
import com.google.gson.Gson
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.psppaypal.PayPalActivityCustomization

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */

internal class NewUiCustomizationManager(val gson : Gson, val sharedPreferences: SharedPreferences) : UiCustomizationManager {
    companion object {
        val PAYPAL_CUSTOMIZATION_KEY = "PaypalCustomization"
    }


    private var payPalActivityCustomization : PayPalActivityCustomization = PayPalActivityCustomization()

    init {
        val paypalPreference = sharedPreferences.getString(PAYPAL_CUSTOMIZATION_KEY, "")
        if (paypalPreference.isNullOrEmpty()) {
            payPalActivityCustomization = PayPalActivityCustomization()
        } else {
            payPalActivityCustomization = gson.fromJson(paypalPreference, payPalActivityCustomization::class.java)
        }

    }



    override fun setPaypalRedirectActivityCustomizations(payPalActivityCustomization: PayPalActivityCustomization) {
        this.payPalActivityCustomization = payPalActivityCustomization
        storeCustomizations()
    }

    internal fun getPaypalRedirectActivityCustomizations() : PayPalActivityCustomization = payPalActivityCustomization



    private fun storeCustomizations() {
        sharedPreferences
                .edit()
                .putString(PAYPAL_CUSTOMIZATION_KEY, gson.toJson(payPalActivityCustomization))
                .commit()
    }

}