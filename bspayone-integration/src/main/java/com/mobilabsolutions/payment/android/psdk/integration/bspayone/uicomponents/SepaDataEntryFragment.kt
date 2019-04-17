package com.mobilabsolutions.payment.android.psdk.integration.bspayone.uicomponents

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.R
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.*
import com.mobilabsolutions.payment.android.psdk.model.SepaData
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

    lateinit var errorDrawable : Drawable


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.sepa_data_entry_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorDrawable = resources.getDrawable(R.drawable.edit_text_frame_error)
        firstNameEditText.getContentOnFocusLost {
           validateFirstName(it)
        }
        ibanNumberEditText.getContentOnFocusLost {
            validateIban(it)
        }
        lastNameEditText.getContentOnFocusLost {
         validateLastName(it)
        }
        countryEditText.setOnClickListener {
            Timber.d("Country selector")
        }

        saveButton.setOnClickListener {
            var success = true
            success = success && validateFirstName(firstNameEditText.getContentsAsString())
            success = success && validateLastName(lastNameEditText.getContentsAsString())
            success = success && validateIban(ibanNumberEditText.getContentsAsString())
            success = success && validateCountry(countryEditText.text.toString())

            if (success) {
                val dataMap : MutableMap<String, String> = mutableMapOf()
                dataMap.put("FIRST_NAME", firstNameEditText.getContentsAsString())
                dataMap.put("LAST_NAME", lastNameEditText.getContentsAsString())
                dataMap.put("IBAN", ibanNumberEditText.getContentsAsString())
                dataMap.put("COUNTRY", countryEditText.text.toString())
                uiComponentHandler.dataSubject.onNext(dataMap)
            }


        }
    }

    fun validateFirstName(name : String) : Boolean {
        val validationResult = personalDataValidator.validateName(name)
        if (!validationResult.success) {
            firstNameEditText.background = errorDrawable
            firstNameEditText.setError(getString(validationResult.errorMessageResourceId), null)
        }
        return validationResult.success
    }

    fun validateLastName(name : String) : Boolean {
        val validationResult = personalDataValidator.validateName(name)
        if (!validationResult.success) {
            lastNameEditText.background = errorDrawable
            lastNameEditText.setError(getString(validationResult.errorMessageResourceId), null)
        }
        return validationResult.success
    }

    fun validateIban(iban : String) : Boolean {
        val validationResult = sepaDataValidator.validateIban(iban)
        if (!validationResult.success) {
            ibanNumberEditText.background = errorDrawable
            ibanNumberEditText.setError(getString(validationResult.errorMessageResourceId), null)
        }
        return validationResult.success
    }

    fun validateCountry(country : String) : Boolean {
        return if (!country.isEmpty()) {
            true
        } else {
            countryEditText.background = errorDrawable
            countryEditText.setError(getString(R.string.validation_error_missing_country), null)
            false
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BsPayoneIntegration.integration?.bsPayoneIntegrationComponent?.inject(this)
        Timber.d("Created")




    }
}