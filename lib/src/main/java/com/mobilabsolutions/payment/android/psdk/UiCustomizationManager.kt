/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk

import android.annotation.SuppressLint
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
 * or you can use the Builder supplied. The values must be reference to the color
 * defined in application resources.
 */
data class PaymentUiConfiguration(
    /**
     * Text color
     */
    @ColorRes val textColor: Int = R.color.gable_green,
    /**
     * Background color
     */
    @ColorRes val backgroundColor: Int = R.color.black_haze,
    /**
     * Button color
     */
    @ColorRes val buttonColor: Int = R.color.lochmara,
    /**
     * Button text color
     */
    @ColorRes val buttonTextColor: Int = R.color.white,
    /**
     * Background color of box containing entry edit text views
     */
    @ColorRes val cellBackgroundColor: Int = R.color.white,
    /**
     * Color of the text inside edit text fields
     */
    @ColorRes val mediumEmphasisColor: Int = R.color.white

) {
    class Builder {
        private var preference = PaymentUiConfiguration()
        /**
         * Text color
         */
        fun setTextColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(textColor = resourceId)
            return this
        }
        /**
         * Background color
         */
        fun setBackgroundColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(backgroundColor = resourceId)
            return this
        }
        /**
         * Button color
         */
        fun setButtonColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(buttonColor = resourceId)
            return this
        }
        /**
         * Button text color
         */
        fun setButtonTextColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(buttonTextColor = resourceId)
            return this
        }
        /**
         * Background color of box containing entry edit text views
         */
        fun setCellBackgroundColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(cellBackgroundColor = resourceId)
            return this
        }
        /**
         * Color of the text inside edit text fields
         */
        fun setMediumEmphasisColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(mediumEmphasisColor = resourceId)
            return this
        }

        fun build(): PaymentUiConfiguration = preference
    }
}

/**
 * UI customization manager allows you to customize Credit card entry screens by selecting specific
 * colors for specific elements
 */
class UiCustomizationManager @Inject internal constructor(val gson: Gson, val sharedPreferences: SharedPreferences) {
    private lateinit var paymentUIConfiguration: PaymentUiConfiguration

    companion object {
        val CUSTOMIZATION_KEY = "Customization"
    }

    init {
        loadPreference()
    }

    fun setCustomizationPreferences(paymentUIConfiguration: PaymentUiConfiguration) {
        this.paymentUIConfiguration = paymentUIConfiguration
        storePreference()
    }

    fun getCustomizationPreferences(): PaymentUiConfiguration {
        return paymentUIConfiguration
    }

    @SuppressLint("ApplySharedPref")
    private fun storePreference() {
        val preferenceJson = gson.toJson(paymentUIConfiguration)
        sharedPreferences.edit().putString(CUSTOMIZATION_KEY, preferenceJson).commit()
    }

    private fun loadPreference() {
        val customizationJson = sharedPreferences.getString(CUSTOMIZATION_KEY, "")!!
        if (customizationJson.isEmpty()) {
            paymentUIConfiguration = PaymentUiConfiguration()
        } else {
            paymentUIConfiguration = gson.fromJson(customizationJson, PaymentUiConfiguration::class.java)
        }
    }
}

/**
 * We provide customizations inside an extension object, so we can limit the visibility of extension
 * functions throughout the project. This way we prevent poisoning the namespace.
 */
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

    /**
     * Apply customizations on any edit text view
     */
    fun EditText.applyEditTextCustomization(paymentUIConfiguration: PaymentUiConfiguration) {
        applyOnTextView(paymentUIConfiguration)
    }

    /**
     * Make the text view look like customized edit text. This is useful for fields that
     * actually open dialogs like expiry date and country selector
     */
    fun TextView.applyFakeEditTextCustomization(paymentUIConfiguration: PaymentUiConfiguration) {
        applyOnTextView(paymentUIConfiguration)
    }

    /**
     * Apply text view background customizations
     */
    private fun TextView.applyOnTextView(paymentUIConfiguration: PaymentUiConfiguration) {
        if (error == null) {
            val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.edit_text_selector)
            val textFieldDrawableContainerState = (backgroundDrawable as StateListDrawable).constantState as DrawableContainer.DrawableContainerState
            val textFieldDrawableStates = textFieldDrawableContainerState.children.filter { it != null }.map { it as GradientDrawable }
            textFieldDrawableStates[0].setStroke(1.px, CustomizationUtil.darken(ContextCompat.getColor(context, R.color.cool_gray)))
            textFieldDrawableStates[1].setStroke(1.px, ContextCompat.getColor(context, R.color.cool_gray))
            textFieldDrawableStates[0].setColor(ContextCompat.getColor(context, paymentUIConfiguration.mediumEmphasisColor))
            textFieldDrawableStates[1].setColor(ContextCompat.getColor(context, paymentUIConfiguration.mediumEmphasisColor))
            background = backgroundDrawable
        } else {
            background = resources.getDrawable(R.drawable.edit_text_frame_error)
        }
        this.applyTextCustomization(paymentUIConfiguration)
    }

    /**
     * Customize text color
     */
    fun TextView.applyTextCustomization(paymentUIConfiguration: PaymentUiConfiguration) {
        setTextColor(ContextCompat.getColor(context, paymentUIConfiguration.textColor))
    }

    /**
     * Apply button customizations
     */
    fun Button.applyCustomization(paymentUIConfiguration: PaymentUiConfiguration) {
        if (isEnabled) {
            val buttonColorDrawable = ContextCompat.getDrawable(context, R.drawable.rounded_corner_button_selector)
            val buttonBackgroundDrawableContainterStates = (buttonColorDrawable as StateListDrawable).constantState as DrawableContainer.DrawableContainerState
            val states = buttonBackgroundDrawableContainterStates.children.filter { it != null }.map { it as GradientDrawable }
            states[0].setColor(CustomizationUtil.lighten(ContextCompat.getColor(context, paymentUIConfiguration.buttonColor)))
            states[1].setColor(ContextCompat.getColor(context, paymentUIConfiguration.buttonColor))
            background = buttonColorDrawable
        } else {
            val buttonColorDrawable = ContextCompat.getDrawable(context, R.drawable.rounded_corner_button_selector_disabled)
            val buttonBackgroundDrawableContainterStates = (buttonColorDrawable as StateListDrawable).constantState as DrawableContainer.DrawableContainerState
            val states = buttonBackgroundDrawableContainterStates.children.filter { it != null }.map { it as GradientDrawable }
            states[0].setColor(ContextCompat.getColor(context, R.color.cool_gray))
            states[1].setColor(ContextCompat.getColor(context, R.color.cool_gray))
            background = buttonColorDrawable
        }
        setTextColor(CustomizationUtil.lighten(ContextCompat.getColor(context, paymentUIConfiguration.buttonTextColor)))
    }

    /**
     * Apply background customization on any view that has ColorDrawable background
     */
    fun View.applyBackgroundCustomization(paymentUIConfiguration: PaymentUiConfiguration) {
        val backgroundColorDrawable = background as ColorDrawable
        backgroundColorDrawable.color = ContextCompat.getColor(context, paymentUIConfiguration.backgroundColor)
        background = backgroundColorDrawable
    }
    /**
     * Apply cell background customization on any view that has ColorDrawable background
     */
    fun View.applyCellBackgroundCustomization(paymentUIConfiguration: PaymentUiConfiguration) {
        val backgroundColorDrawable = background as GradientDrawable
        backgroundColorDrawable.setColor(ContextCompat.getColor(context, paymentUIConfiguration.cellBackgroundColor))
        background = backgroundColorDrawable
    }

    /**
     * Request focus for this view and show IME
     */
    fun View.showKeyboardAndFocus() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(this, 0)
    }
}

/**
 * Convert pixels to density pixels
 */
val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
/**
 * Convert density pixels to pixels
 */
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
