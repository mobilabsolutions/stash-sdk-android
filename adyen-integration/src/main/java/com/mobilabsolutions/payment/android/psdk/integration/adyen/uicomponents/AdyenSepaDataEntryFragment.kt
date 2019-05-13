package com.mobilabsolutions.payment.android.psdk.integration.adyen.uicomponents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mobilabsolutions.payment.android.psdk.CustomizationPreference
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.applyBackgroundCustomization
import com.mobilabsolutions.payment.android.psdk.applyCellBackgroundCustomization
import com.mobilabsolutions.payment.android.psdk.applyCustomization
import com.mobilabsolutions.payment.android.psdk.applyEditTextCustomization
import com.mobilabsolutions.payment.android.psdk.applyFakeEditTextCustomization
import com.mobilabsolutions.payment.android.psdk.applyTextCustomization
import com.mobilabsolutions.payment.android.psdk.integration.adyen.AdyenIntegration
import com.mobilabsolutions.payment.android.psdk.integration.adyen.R
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PersonalDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.SepaDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.getContentOnFocusLost
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.back
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.* // ktlint-disable no-wildcard-imports
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.countryText
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.firstNameEditText
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.firstNameTitleTextView
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.lastNameEditText
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.lastNameTitleTextView
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.saveButton
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.countryTitleTextView
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class AdyenSepaDataEntryFragment : Fragment() {

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

    private var viewState: SepaDataEntryViewState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AdyenIntegration.integration?.adyenIntegrationComponent?.inject(this)
        Timber.d("Created")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.adyen_sepa_data_entry_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        countryText.visibility = View.GONE
        countryTitleTextView.visibility = View.GONE

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

        countryText.setOnClickListener {
            Timber.d("Country selector")
        }

        saveButton.setOnClickListener {
            viewState?.let {
                val dataMap: MutableMap<String, String> = mutableMapOf()
                dataMap[SepaData.FIRST_NAME] = it.firstName
                dataMap[SepaData.LAST_NAME] = it.lastName
                dataMap[SepaData.IBAN] = it.iban
                uiComponentHandler.dataSubject.onNext(dataMap)
            }
        }

        back.setOnClickListener {
            requireActivity().onBackPressed()
        }
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

    data class SepaDataEntryViewState(
        val firstName: String = "",
        val lastName: String = "",
        val iban: String = ""
    )
}