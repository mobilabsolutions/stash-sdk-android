package com.mobilabsolutions.payment.android.psdk.internal

import android.content.SharedPreferences
import com.google.gson.Gson
import com.mobilabsolutions.payment.android.R
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.psppaypal.PayPalActivityCustomization
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */

data class CustomizationPreference(
        val textColor : Int = R.color.dark_two,
        val backgroundColor: Int = R.color.unknown_blue,
        val buttonColor : Int = R.color.unknown_blue,
        val buttonTextColor : Int = R.color.white,
        val cellBackgroundColor : Int = R.color.white,
        val mediumEmphasisColor : Int = R.color.cool_gray


)

internal class NewUiCustomizationManager @Inject constructor(val gson: Gson, val sharedPreferences: SharedPreferences) : UiCustomizationManager {
    lateinit var customizationPreference: CustomizationPreference

    companion object {
        val CUSTOMIZATION_KEY = "Customization"
    }

    init {
        loadPreference()
    }

    override fun setBackgroundColor(color: Int) {
        customizationPreference = customizationPreference.copy(backgroundColor = color)
        storePreference()
    }

    override fun getBackgroundColor() : Int{
        return customizationPreference.backgroundColor
    }

    private fun storePreference() {
        val preferenceJson = gson.toJson(customizationPreference)
    }

    private fun loadPreference() {
        val customizationJson = sharedPreferences.getString(CUSTOMIZATION_KEY, "")!!
        if (customizationJson.isEmpty()){
            customizationPreference = CustomizationPreference()
        } else {
            customizationPreference = gson.fromJson(customizationJson, CustomizationPreference::class.java)
        }

    }

}