package com.mobilabsolutions.payment.android.psdk.internal

import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.DrawableContainer
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.google.gson.Gson
import com.mobilabsolutions.payment.android.R
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */

/**
 * A class representing a UI customization preferences. Default values are provided,
 * or you can use the Builder supplied
 */
data class CustomizationPreference(
    val textColor: Int = R.color.dark_two,
    val backgroundColor: Int = R.color.ice_blue_two,
    val buttonColor: Int = R.color.unknown_blue,
    val buttonTextColor: Int = R.color.white,
    val cellBackgroundColor: Int = R.color.white,
    val mediumEmphasisColor: Int = R.color.cool_gray

) {
    class Builder() {
        private var preference = CustomizationPreference()
        fun setTextColor(resourceId: Int) {
            preference = preference.copy(textColor = resourceId)
        }

        fun setBackgroundColor(resourceId: Int) {
            preference = preference.copy(backgroundColor = resourceId)
        }

        fun setButtonColor(resourceId: Int) {
            preference = preference.copy(buttonColor = resourceId)
        }

        fun setButtonTextColor(resourceId: Int) {
            preference = preference.copy(buttonTextColor = resourceId)
        }

        fun setCellBackgroundColor(resourceId: Int) {
            preference = preference.copy(cellBackgroundColor = resourceId)
        }

        fun setMediumEmphasisColor(resourceId: Int) {
            preference = preference.copy(mediumEmphasisColor = resourceId)
        }

        fun build(): CustomizationPreference = preference
    }
}

/**
 * UI customization manager allows you to customize Credit card entry screens by selecting specific
 * colors for specific elements
 */
class UiCustomizationManager @Inject internal constructor(val gson: Gson, val sharedPreferences: SharedPreferences) {
    private lateinit var customizationPreference: CustomizationPreference

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

    fun getCustomizationPreferences(): CustomizationPreference {
        return customizationPreference
    }

    private fun storePreference() {
        val preferenceJson = gson.toJson(customizationPreference)
        sharedPreferences.edit().putString(CUSTOMIZATION_KEY, preferenceJson).commit()
    }

    private fun loadPreference() {
        val customizationJson = sharedPreferences.getString(CUSTOMIZATION_KEY, "")!!
        if (customizationJson.isEmpty()) {
            customizationPreference = CustomizationPreference()
        } else {
            customizationPreference = gson.fromJson(customizationJson, CustomizationPreference::class.java)
        }
    }
}

object CustomizationUtil {

    fun darken(color: Int): Int {
        return ColorUtils.blendARGB(color, Color.BLACK, 0.2f)
    }

    fun lighten(color: Int): Int {
        return ColorUtils.blendARGB(color, Color.WHITE, 0.2f)
    }
}

fun EditText.applyEditTextCustomization(customizationPreference: CustomizationPreference) {
    applyOnTextView(customizationPreference)
}

fun TextView.applyFakeEditTextCustomization(customizationPreference: CustomizationPreference) {
    applyOnTextView(customizationPreference)
}

private fun TextView.applyOnTextView(customizationPreference: CustomizationPreference) {
    if (error == null) {
        val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.edit_text_selector)
        val textFieldDrawableContainerState = (backgroundDrawable as StateListDrawable).constantState as DrawableContainer.DrawableContainerState
        val textFieldDrawableStates = textFieldDrawableContainerState.children.filter { it != null }.map { it as GradientDrawable }
        textFieldDrawableStates[0].setStroke(1.px, ContextCompat.getColor(context, customizationPreference.mediumEmphasisColor))
        textFieldDrawableStates[1].setStroke(1.px, CustomizationUtil.darken(ContextCompat.getColor(context, customizationPreference.mediumEmphasisColor)))
        background = backgroundDrawable
    } else {
        background = resources.getDrawable(R.drawable.edit_text_frame_error)
    }
    this.applyTextCustomization(customizationPreference)
}

fun TextView.applyTextCustomization(customizationPreference: CustomizationPreference) {
    setTextColor(ContextCompat.getColor(context, customizationPreference.textColor))
}

fun Button.applyCustomization(customizationPreference: CustomizationPreference) {
    if (isEnabled) {
        val buttonColorDrawable = ContextCompat.getDrawable(context, R.drawable.rounded_corner_button_selector)
        val buttonBackgroundDrawableContainterStates = (buttonColorDrawable as StateListDrawable).constantState as DrawableContainer.DrawableContainerState
        val states = buttonBackgroundDrawableContainterStates.children.filter { it != null }.map { it as GradientDrawable }
        states[0].setColor(CustomizationUtil.lighten(ContextCompat.getColor(context, customizationPreference.buttonColor)))
        states[1].setColor(ContextCompat.getColor(context, customizationPreference.buttonColor))
        background = buttonColorDrawable
    } else {
        val buttonColorDrawable = ContextCompat.getDrawable(context, R.drawable.rounded_corner_button_selector_disabled)
        background = buttonColorDrawable
    }
    setTextColor(CustomizationUtil.lighten(ContextCompat.getColor(context, customizationPreference.buttonTextColor)))
}

fun View.applyBackgroundCustomization(customizationPreference: CustomizationPreference) {
    background = ContextCompat.getDrawable(context, customizationPreference.backgroundColor)
}

fun View.applyCellBackgroundCustomization(customizationPreference: CustomizationPreference) {
    background = ContextCompat.getDrawable(context, customizationPreference.cellBackgroundColor)
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()