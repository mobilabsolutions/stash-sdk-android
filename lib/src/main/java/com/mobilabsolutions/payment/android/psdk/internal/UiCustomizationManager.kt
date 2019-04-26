package com.mobilabsolutions.payment.android.psdk.internal

import android.content.SharedPreferences
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.google.gson.Gson
import com.mobilabsolutions.payment.android.R
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */

data class CustomizationPreference(
        val textColor : Int = R.color.dark_two,
        val backgroundColor: Int = R.color.ice_blue_two,
        val buttonColor : Int = R.color.unknown_blue,
        val buttonTextColor : Int = R.color.white,
        val cellBackgroundColor : Int = R.color.white,
//        val mediumEmphasisColor : Int = R.color.cool_gray
        val mediumEmphasisColor : Int = android.R.color.holo_green_light


)

class UiCustomizationManager @Inject internal constructor(val gson: Gson, val sharedPreferences: SharedPreferences) {
    lateinit var customizationPreference: CustomizationPreference

    companion object {
        val CUSTOMIZATION_KEY = "Customization"
    }

    init {
        loadPreference()
    }

    fun setCustomizationPreferences(customizationPreference: CustomizationPreference) {
        this.customizationPreference = customizationPreference
        storePreference()
    }

    fun getCustomizationPreferences() : CustomizationPreference {
        return customizationPreference
    }

    private fun storePreference() {
        val preferenceJson = gson.toJson(customizationPreference)
        sharedPreferences.edit().putString(CUSTOMIZATION_KEY, preferenceJson).commit()
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

object CustomizationUtil {

    fun darken(color : Int) : Int {
        return ColorUtils.blendARGB(color, Color.BLACK, 0.2f)
    }
}