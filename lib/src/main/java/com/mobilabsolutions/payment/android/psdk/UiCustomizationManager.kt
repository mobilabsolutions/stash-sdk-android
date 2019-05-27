package com.mobilabsolutions.payment.android.psdk

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.DrawableContainer
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
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
data class PaymentUIConfiguration(
    @ColorRes val textColor: Int = R.color.gable_green,
    @ColorRes val backgroundColor: Int = R.color.black_haze,
    @ColorRes val buttonColor: Int = R.color.lochmara,
    @ColorRes val buttonTextColor: Int = R.color.white,
    @ColorRes val cellBackgroundColor: Int = R.color.white,
    @ColorRes val mediumEmphasisColor: Int = R.color.cool_gray

) {
    class Builder {
        private var preference = PaymentUIConfiguration()
        fun setTextColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(textColor = resourceId)
            return this
        }

        fun setBackgroundColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(backgroundColor = resourceId)
            return this
        }

        fun setButtonColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(buttonColor = resourceId)
            return this
        }

        fun setButtonTextColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(buttonTextColor = resourceId)
            return this
        }

        fun setCellBackgroundColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(cellBackgroundColor = resourceId)
            return this
        }

        fun setMediumEmphasisColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(mediumEmphasisColor = resourceId)
            return this
        }

        fun build(): PaymentUIConfiguration = preference
    }
}

/**
 * UI customization manager allows you to customize Credit card entry screens by selecting specific
 * colors for specific elements
 */
class UiCustomizationManager @Inject internal constructor(val gson: Gson, val sharedPreferences: SharedPreferences) {
    private lateinit var paymentUIConfiguration: PaymentUIConfiguration

    companion object {
        val CUSTOMIZATION_KEY = "Customization"
    }

    init {
        loadPreference()
    }

    fun setCustomizationPreferences(paymentUIConfiguration: PaymentUIConfiguration) {
        this.paymentUIConfiguration = paymentUIConfiguration
        storePreference()
    }

    fun getCustomizationPreferences(): PaymentUIConfiguration {
        return paymentUIConfiguration
    }

    private fun storePreference() {
        val preferenceJson = gson.toJson(paymentUIConfiguration)
        sharedPreferences.edit().putString(CUSTOMIZATION_KEY, preferenceJson).commit()
    }

    private fun loadPreference() {
        val customizationJson = sharedPreferences.getString(CUSTOMIZATION_KEY, "")!!
        if (customizationJson.isEmpty()) {
            paymentUIConfiguration = PaymentUIConfiguration()
        } else {
            paymentUIConfiguration = gson.fromJson(customizationJson, PaymentUIConfiguration::class.java)
        }
    }
}

object CustomizationExtensions {

    operator fun invoke(body: CustomizationExtensions.() -> Unit): Unit = body.invoke(this)

    object CustomizationUtil {

        fun darken(color: Int): Int {
            return ColorUtils.blendARGB(color, Color.BLACK, 0.5f)
        }

        fun lighten(color: Int): Int {
            return ColorUtils.blendARGB(color, Color.WHITE, 0.2f)
        }
    }

    fun EditText.applyEditTextCustomization(paymentUIConfiguration: PaymentUIConfiguration) {
        applyOnTextView(paymentUIConfiguration)
    }

    fun TextView.applyFakeEditTextCustomization(paymentUIConfiguration: PaymentUIConfiguration) {
        applyOnTextView(paymentUIConfiguration)
    }

    private fun TextView.applyOnTextView(paymentUIConfiguration: PaymentUIConfiguration) {
        if (error == null) {
            val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.edit_text_selector)
            val textFieldDrawableContainerState = (backgroundDrawable as StateListDrawable).constantState as DrawableContainer.DrawableContainerState
            val textFieldDrawableStates = textFieldDrawableContainerState.children.filter { it != null }.map { it as GradientDrawable }
            textFieldDrawableStates[0].setStroke(1.px, CustomizationUtil.darken(ContextCompat.getColor(context, paymentUIConfiguration.mediumEmphasisColor)))
            textFieldDrawableStates[1].setStroke(1.px, ContextCompat.getColor(context, paymentUIConfiguration.mediumEmphasisColor))
            background = backgroundDrawable
        } else {
            background = resources.getDrawable(R.drawable.edit_text_frame_error)
        }
        this.applyTextCustomization(paymentUIConfiguration)
    }

    fun TextView.applyTextCustomization(paymentUIConfiguration: PaymentUIConfiguration) {
        setTextColor(ContextCompat.getColor(context, paymentUIConfiguration.textColor))
    }

    fun Button.applyCustomization(paymentUIConfiguration: PaymentUIConfiguration) {
        if (isEnabled) {
            val buttonColorDrawable = ContextCompat.getDrawable(context, R.drawable.rounded_corner_button_selector)
            val buttonBackgroundDrawableContainterStates = (buttonColorDrawable as StateListDrawable).constantState as DrawableContainer.DrawableContainerState
            val states = buttonBackgroundDrawableContainterStates.children.filter { it != null }.map { it as GradientDrawable }
            states[0].setColor(CustomizationUtil.lighten(ContextCompat.getColor(context, paymentUIConfiguration.buttonColor)))
            states[1].setColor(ContextCompat.getColor(context, paymentUIConfiguration.buttonColor))
            background = buttonColorDrawable
        } else {
            val buttonColorDrawable = ContextCompat.getDrawable(context, R.drawable.rounded_corner_button_selector_disabled)
            background = buttonColorDrawable
        }
        setTextColor(CustomizationUtil.lighten(ContextCompat.getColor(context, paymentUIConfiguration.buttonTextColor)))
    }

    fun View.applyBackgroundCustomization(paymentUIConfiguration: PaymentUIConfiguration) {
        val backgroundColorDrawable = background as ColorDrawable
        backgroundColorDrawable.color = ContextCompat.getColor(context, paymentUIConfiguration.backgroundColor)
        background = backgroundColorDrawable
    }

    fun View.applyCellBackgroundCustomization(paymentUIConfiguration: PaymentUIConfiguration) {
        val backgroundColorDrawable = background as GradientDrawable
        backgroundColorDrawable.setColor(ContextCompat.getColor(context, paymentUIConfiguration.cellBackgroundColor))
        background = backgroundColorDrawable
    }

    fun View.showKeyboardAndFocus() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(this, 0)
    }
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
