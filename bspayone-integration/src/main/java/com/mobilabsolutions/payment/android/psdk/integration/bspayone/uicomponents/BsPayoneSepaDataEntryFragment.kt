package com.mobilabsolutions.payment.android.psdk.integration.bspayone.uicomponents

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.R
import com.mobilabsolutions.payment.android.psdk.internal.CustomizationPreference
import com.mobilabsolutions.payment.android.psdk.internal.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.internal.applyBackgroundCustomization
import com.mobilabsolutions.payment.android.psdk.internal.applyCellBackgroundCustomization
import com.mobilabsolutions.payment.android.psdk.internal.applyCustomization
import com.mobilabsolutions.payment.android.psdk.internal.applyEditTextCustomization
import com.mobilabsolutions.payment.android.psdk.internal.applyFakeEditTextCustomization
import com.mobilabsolutions.payment.android.psdk.internal.applyTextCustomization
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.Country
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.CountryChooserActivity
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PersonalDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.SepaDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.getContentOnFocusLost
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import com.mobilabsolutions.payment.android.util.CountryDetectorUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.countryText
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.countryTitleTextView
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.errorIban
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.firstNameEditText
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.firstNameTitleTextView
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.ibanNumberEditText
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.ibanTitleTextView
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.lastNameEditText
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.lastNameTitleTextView
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.saveButton
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.sepaScreenCellLayout
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.sepaScreenMainLayout
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.sepaScreenTitle
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BsPayoneSepaDataEntryFragment : Fragment() {

    @Inject
    lateinit var uiComponentHandler: UiComponentHandler

    lateinit var customizationPreference: CustomizationPreference

    @Inject
    lateinit var sepaDataValidator: SepaDataValidator

    @Inject
    lateinit var personalDataValidator: PersonalDataValidator

    @Inject
    lateinit var uiCustomizationManager: UiCustomizationManager

    private val disposables = CompositeDisposable()
    private val firstNameSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val lastNameSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val ibanSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val countrySubject: BehaviorSubject<String> = BehaviorSubject.create()

    private var viewState: SepaDataEntryViewState? = null

    private lateinit var suggestedCountry: Locale

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BsPayoneIntegration.integration?.bsPayoneIntegrationComponent?.inject(this)
        suggestedCountry = CountryDetectorUtil.getBestGuessAtCurrentCountry(requireContext())
        Timber.d("Created")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sepa_data_entry_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customizationPreference = uiCustomizationManager.getCustomizationPreferences()

        ibanTitleTextView.applyTextCustomization(customizationPreference)
        firstNameTitleTextView.applyTextCustomization(customizationPreference)
        lastNameTitleTextView.applyTextCustomization(customizationPreference)
        sepaScreenTitle.applyTextCustomization(customizationPreference)
        countryTitleTextView.applyTextCustomization(customizationPreference)

        firstNameEditText.applyEditTextCustomization(customizationPreference)
        lastNameEditText.applyEditTextCustomization(customizationPreference)
        countryText.applyFakeEditTextCustomization(customizationPreference)
        ibanNumberEditText.applyEditTextCustomization(customizationPreference)
        saveButton.applyCustomization(customizationPreference)
        sepaScreenMainLayout.applyBackgroundCustomization(customizationPreference)
        sepaScreenCellLayout.applyCellBackgroundCustomization(customizationPreference)

        disposables += Observables.combineLatest(
                firstNameSubject,
                lastNameSubject,
                ibanSubject,
                countrySubject,
                ::SepaDataEntryViewState)
                .subscribe(this::onViewStateNext)

        disposables += firstNameSubject
                .doOnNext {
                    validateFirstName(it)
                }
                .subscribe()

        disposables += lastNameSubject
                .doOnNext {
                    validateLastName(it)
                }
                .subscribe()

        disposables += ibanSubject
                .doOnNext {
                    validateIban(it)
                }
                .subscribe()

        firstNameEditText.getContentOnFocusLost { firstNameSubject.onNext(it.trim()) }
        lastNameEditText.getContentOnFocusLost { lastNameSubject.onNext(it.trim()) }
        ibanNumberEditText.getContentOnFocusLost { ibanSubject.onNext(it.trim()) }
        countryText.onTextChanged { countrySubject.onNext(it.toString().trim()) }

        countryText.setOnClickListener {
            startActivityForResult(Intent(context, CountryChooserActivity::class.java)
                    .putExtra(CountryChooserActivity.CURRENT_LOCATION_ENABLE_EXTRA, true)
                    .putExtra(CountryChooserActivity.CURRENT_LOCATION_CUSTOM_EXTRA, suggestedCountry.country), 1)
        }

        saveButton.setOnClickListener {
            viewState?.let {
                val dataMap: MutableMap<String, String> = mutableMapOf()
                dataMap[SepaData.FIRST_NAME] = it.firstName
                dataMap[SepaData.LAST_NAME] = it.lastName
                dataMap[SepaData.IBAN] = it.iban
                dataMap[BillingData.COUNTRY] = it.country
                uiComponentHandler.dataSubject.onNext(dataMap)
            }
        }

        countryText.text = suggestedCountry.displayCountry
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    private fun onViewStateNext(state: SepaDataEntryViewState) {
        this.viewState = state
        var success = true
        success = validateFirstName(state.firstName) && success
        success = validateLastName(state.lastName) && success
        success = validateIban(state.iban) && success
        success = validateCountry(state.country) && success
        saveButton.isEnabled = success
        saveButton.applyCustomization(customizationPreference)
    }

    private fun EditText.customError(message: String) {
        if (this.error == null) {
            this.setError(message, ContextCompat.getDrawable(requireContext(), R.drawable.empty_drawable))
            this.applyEditTextCustomization(customizationPreference)
        }
    }

    private fun EditText.clearError() {
        this.error = null
        this.applyEditTextCustomization(customizationPreference)
    }

    private fun validateFirstName(name: String): Boolean {
        val validationResult = personalDataValidator.validateName(name)
        if (!validationResult.success) {
            firstNameEditText.customError(getString(validationResult.errorMessageResourceId))
        } else {
            firstNameEditText.clearError()
        }
        return validationResult.success
    }

    private fun validateLastName(name: String): Boolean {
        val validationResult = personalDataValidator.validateName(name)
        if (!validationResult.success) {
            lastNameEditText.customError(getString(validationResult.errorMessageResourceId))
        } else {
            lastNameEditText.clearError()
        }
        return validationResult.success
    }

    private fun validateIban(iban: String): Boolean {
        val validationResult = sepaDataValidator.validateIban(iban)
        if (!validationResult.success) {
            ibanNumberEditText.setBackgroundResource(R.drawable.edit_text_frame_error)
            errorIban.visibility = View.VISIBLE
        } else {
            ibanNumberEditText.setBackgroundResource(R.drawable.edit_text_frame)
            errorIban.visibility = View.GONE
        }
        return validationResult.success
    }

    private fun validateCountry(country: String): Boolean {
        return if (country.isNotEmpty()) {
            countryText.error = null
            countryText.applyFakeEditTextCustomization(customizationPreference)
            true
        } else {
            countryText.setError(getString(R.string.validation_error_missing_country), ContextCompat.getDrawable(requireContext(), R.drawable.empty_drawable))
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
                data?.getParcelableExtra<Country>(CountryChooserActivity.SELECTED_COUNTRY)?.let {
                    countryText.text = it.displayName
                }
            }
        } catch (ex: Exception) {
            Toast.makeText(activity, ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    data class SepaDataEntryViewState(
        val firstName: String = "",
        val lastName: String = "",
        val iban: String = "",
        val country: String = ""
    )
}