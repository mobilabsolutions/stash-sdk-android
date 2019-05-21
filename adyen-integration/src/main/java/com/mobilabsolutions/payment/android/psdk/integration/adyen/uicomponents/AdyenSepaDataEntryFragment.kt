package com.mobilabsolutions.payment.android.psdk.integration.adyen.uicomponents

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobilabsolutions.payment.android.psdk.CustomizationExtensions
import com.mobilabsolutions.payment.android.psdk.CustomizationPreference
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.integration.adyen.AdyenIntegration
import com.mobilabsolutions.payment.android.psdk.integration.adyen.R
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PersonalDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.SepaDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.ValidationResult
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.getContentOnFocusLost
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.observeText
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.back
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.countryText
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.countryTitleTextView
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.errorIban
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.errorSepaFirstName
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.errorSepaLastName
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.firstNameEditText
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.firstNameTitleTextView
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.ibanNumberEditText
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.ibanTitleTextView
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.lastNameEditText
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.lastNameTitleTextView
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.saveButton
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.sepaScreenCellLayout
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.sepaScreenMainLayout
import kotlinx.android.synthetic.main.adyen_sepa_data_entry_fragment.sepaScreenTitle
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

    private val firstNameLostFocusSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val firstNameTextChangedSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private val lastNameLostFocusSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val lastNameTextChangedSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private val ibanLostFocusSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val ibanTextChangedSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private val countrySubject: BehaviorSubject<String> = BehaviorSubject.create()

    private var viewState: SepaDataEntryViewState? = null

    private var waitTimer: CountDownTimer? = null

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

        CustomizationExtensions {
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

            firstNameEditText.showKeyboardAndFocus()
        }

        disposables += Observables.combineLatest(
            firstNameTextChangedSubject,
            lastNameTextChangedSubject,
            ibanTextChangedSubject,
            ::SepaDataEntryViewState)
            .subscribe(this::onViewStateNext)

        disposables += firstNameLostFocusSubject
            .doOnNext {
                validateFirstNameAndUpdateUI(it, false)
            }
            .subscribe()

        disposables += firstNameTextChangedSubject
            .doOnNext {
                validateFirstNameAndUpdateUI(it, true)
            }
            .subscribe()

        disposables += lastNameLostFocusSubject
            .doOnNext {
                validateLastNameAndUpdateUI(it, false)
            }
            .subscribe()

        disposables += lastNameTextChangedSubject
            .doOnNext {
                validateLastNameAndUpdateUI(it, true)
            }
            .subscribe()

        disposables += ibanLostFocusSubject
            .doOnNext {
                validateIbanAndUpdateUI(it, false)
            }
            .subscribe()

        disposables += ibanTextChangedSubject
            .doOnNext {
                validateIbanAndUpdateUI(it, true)
            }
            .subscribe()

        firstNameEditText.getContentOnFocusLost { firstNameLostFocusSubject.onNext(it.trim()) }
        firstNameEditText.observeText { firstNameTextChangedSubject.onNext(it.trim()) }

        lastNameEditText.getContentOnFocusLost { lastNameLostFocusSubject.onNext(it.trim()) }
        lastNameEditText.observeText { lastNameTextChangedSubject.onNext(it.trim()) }

        ibanNumberEditText.getContentOnFocusLost { ibanLostFocusSubject.onNext(it.trim()) }
        ibanNumberEditText.observeText { ibanTextChangedSubject.onNext(it.trim()) }

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
        success = validateName(state.firstName).success && success
        success = validateName(state.lastName).success && success
        success = validateIban(state.iban).success && success
        saveButton.isEnabled = success
        CustomizationExtensions {
            saveButton.applyCustomization(customizationPreference)
        }
    }

    private fun validateFirstNameAndUpdateUI(name: String, isDelayed: Boolean): Boolean {
        val validationResult = validateName(name)
        if (!validationResult.success) {
            if (isDelayed) {
                stopTimer()
                startTimer(firstNameEditText, errorSepaFirstName)
            } else {
                showError(firstNameEditText, errorSepaFirstName)
            }
        } else {
            stopTimer()
            hideError(firstNameEditText, errorSepaFirstName)
        }
        return validationResult.success
    }

    private fun validateLastNameAndUpdateUI(name: String, isDelayed: Boolean): Boolean {
        val validationResult = validateName(name)
        if (!validationResult.success) {
            if (isDelayed) {
                stopTimer()
                startTimer(lastNameEditText, errorSepaLastName)
            } else {
                showError(lastNameEditText, errorSepaLastName)
            }
        } else {
            stopTimer()
            hideError(lastNameEditText, errorSepaLastName)
        }
        return validationResult.success
    }

    private fun validateName(name: String): ValidationResult {
        return personalDataValidator.validateName(name)
    }

    private fun validateIbanAndUpdateUI(iban: String, isDelayed: Boolean): Boolean {
        val validationResult = validateIban(iban)
        if (!validationResult.success) {
            if (isDelayed) {
                stopTimer()
                startTimer(ibanNumberEditText, errorIban)
            } else {
                showError(ibanNumberEditText, errorIban)
            }
        } else {
            stopTimer()
            hideError(ibanNumberEditText, errorIban)
        }
        return validationResult.success
    }

    private fun validateIban(iban: String): ValidationResult {
        return sepaDataValidator.validateIban(iban)
    }


    private fun startTimer(sourceView: View, errorView: View) {
        waitTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Do Nothing
            }

            override fun onFinish() {
                showError(sourceView, errorView)
            }
        }.start()
    }

    private fun showError(sourceView: View, errorView: View) {
        errorView.visibility = View.VISIBLE
        sourceView.setBackgroundResource(R.drawable.edit_text_frame_error)
    }

    private fun hideError(sourceView: View, errorView: View) {
        sourceView.setBackgroundResource(R.drawable.edit_text_frame)
        errorView.visibility = View.GONE
    }

    private fun stopTimer() {
        waitTimer?.cancel()
        waitTimer = null
    }

    data class SepaDataEntryViewState(
        val firstName: String = "",
        val lastName: String = "",
        val iban: String = ""
    )
}