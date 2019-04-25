package com.mobilabsolutions.payment.android.psdk.integration.bspayone.uicomponents

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableContainer
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.R
import com.mobilabsolutions.payment.android.psdk.internal.CustomizationUtil.darken
import com.mobilabsolutions.payment.android.psdk.internal.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PersonalDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.SepaDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.getContentOnFocusLost
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.getContentsAsString
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
// ktlint-disable no-wildcard-imports
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.*
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class SepaDataEntryFragment : Fragment() {

    @Inject
    lateinit var uiComponentHandler: UiComponentHandler

    @Inject
    lateinit var sepaDataValidator: SepaDataValidator

    @Inject
    lateinit var personalDataValidator: PersonalDataValidator

    @Inject
    lateinit var uiCustomizationManager: UiCustomizationManager

    lateinit var textFieldBackgroundErrorDrawable: Drawable
    lateinit var textFieldBackgroundNormalDrawable: Drawable
    var textColor : Int = 0
    lateinit var buttonColorDrawable : Drawable
    var buttonTextColor : Int = 0
    var cellBackgroundColor : Int = 0
    var mediumEmphasisColor : Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sepa_data_entry_fragment, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textFieldBackgroundErrorDrawable = resources.getDrawable(R.drawable.edit_text_frame_error)
        textFieldBackgroundNormalDrawable = resources.getDrawable(R.drawable.edit_text_selector)
        buttonColorDrawable = resources.getDrawable(R.drawable.rounded_corner_button_selector)



        val textFieldDrawableContainerState = (textFieldBackgroundNormalDrawable as StateListDrawable).constantState as DrawableContainer.DrawableContainerState
        val textFieldDrawableStates = textFieldDrawableContainerState.children.map { it as GradientDrawable }
        textFieldDrawableStates[0].setStroke(1, resources.getColor(uiCustomizationManager.getCustomizationPreferences().mediumEmphasisColor))
        textFieldDrawableStates[1].setStroke(1, darken(resources.getColor(uiCustomizationManager.getCustomizationPreferences().mediumEmphasisColor)))

        val buttonBackgroundDrawableContainterStates = (buttonColorDrawable as StateListDrawable).constantState as DrawableContainer.DrawableContainerState
        val states = buttonBackgroundDrawableContainterStates.children
        (states[0] as GradientDrawable).setStroke(10, Color.CYAN)
        (states[1] as GradientDrawable).setStroke(5, Color.RED)






        firstNameEditText.getContentOnFocusLost {
            validateFirstName(it)
        }
        firstNameEditText.refreshCustomizations()

        ibanAccount.getContentOnFocusLost {
            validateIban(it)
        }
        ibanAccount.refreshCustomizations()


        lastNameEditText.getContentOnFocusLost {
            validateLastName(it)
        }
        lastNameEditText.refreshCustomizations()


        countryText.setOnClickListener {
            Timber.d("Country selector")
        }
        countryText.refreshCustomizations()


        saveButton.setOnClickListener {
            var success = true
            success = validateFirstName(firstNameEditText.getContentsAsString()) && success
            success = validateLastName(lastNameEditText.getContentsAsString()) && success
            success = validateIban(ibanAccount.getContentsAsString()) && success
            success = validateCountry(countryText.text.toString()) && success

            if (success) {
                val dataMap: MutableMap<String, String> = mutableMapOf()
                dataMap.put(SepaData.FIRST_NAME, firstNameEditText.getContentsAsString())
                dataMap.put(SepaData.LAST_NAME, lastNameEditText.getContentsAsString())
                dataMap.put(SepaData.IBAN, ibanAccount.getContentsAsString())
                dataMap.put(BillingData.COUNTRY, countryText.text.toString())
                uiComponentHandler.dataSubject.onNext(dataMap)
            }
        }
    }

    private fun TextView.customError(message: String) {
        if (this.error == null) {
            this.setBackgroundResource(R.drawable.edit_text_frame_error)
            this.setError(message, resources.getDrawable(R.drawable.empty_drawable))
            this.invalidate()
        }
    }

    private fun TextView.clearError() {
        background = textFieldBackgroundNormalDrawable
//        this.setBackgroundResource(R.drawable.edit_text_frame)
        this.error = null
    }

    private fun TextView.refreshCustomizations() {
        if (this.error == null) {
            background = textFieldBackgroundNormalDrawable
        }
    }

    fun validateFirstName(name: String): Boolean {
        val validationResult = personalDataValidator.validateName(name)
        if (!validationResult.success) {
            firstNameEditText.customError(getString(validationResult.errorMessageResourceId))
        } else {
            firstNameEditText.clearError()
        }
        return validationResult.success
    }

    fun validateLastName(name: String): Boolean {
        val validationResult = personalDataValidator.validateName(name)
        if (!validationResult.success) {
            lastNameEditText.customError(getString(validationResult.errorMessageResourceId))
        } else {
            lastNameEditText.clearError()
        }
        return validationResult.success
    }

    fun validateIban(iban: String): Boolean {
        val validationResult = sepaDataValidator.validateIban(iban)
        if (!validationResult.success) {
            ibanAccount.customError(getString(validationResult.errorMessageResourceId))
        } else {
            ibanAccount.clearError()
        }
        return validationResult.success
    }

    fun validateCountry(country: String): Boolean {
        return if (!country.isEmpty()) {
            countryText.clearError()
            true
        } else {
            countryText.customError(getString(R.string.validation_error_missing_country))
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BsPayoneIntegration.integration?.bsPayoneIntegrationComponent?.inject(this)
        Timber.d("Created")
    }
}