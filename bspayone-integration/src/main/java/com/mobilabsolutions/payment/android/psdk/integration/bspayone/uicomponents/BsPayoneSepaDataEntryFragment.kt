package com.mobilabsolutions.payment.android.psdk.integration.bspayone.uicomponents

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mobilabsolutions.payment.android.psdk.CustomizationExtensions
import com.mobilabsolutions.payment.android.psdk.PaymentUIConfiguration
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.R
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.Country
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.CountryChooserActivity
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PersonalDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.SepaDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.SnackBarExtensions
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.UiRequestHandler
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.ValidationResult
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.getContentOnFocusLost
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.observeText
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import com.mobilabsolutions.payment.android.util.CountryDetectorUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.back
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.bsPayoneSepaEntrySwipeRefresh
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.countryText
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.countryTitleTextView
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.errorIban
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.errorSepaFirstName
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.errorSepaLastName
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

    companion object {
        private const val COUNTRY_REQUEST_CODE = 1
    }

    @Inject
    lateinit var uiComponentHandler: UiComponentHandler

    lateinit var paymentUIConfiguration: PaymentUIConfiguration

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

    private lateinit var suggestedCountry: Locale

    private var waitTimer: CountDownTimer? = null

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
        paymentUIConfiguration = uiCustomizationManager.getCustomizationPreferences()

        CustomizationExtensions {
            ibanTitleTextView.applyTextCustomization(paymentUIConfiguration)
            firstNameTitleTextView.applyTextCustomization(paymentUIConfiguration)
            lastNameTitleTextView.applyTextCustomization(paymentUIConfiguration)
            sepaScreenTitle.applyTextCustomization(paymentUIConfiguration)
            countryTitleTextView.applyTextCustomization(paymentUIConfiguration)

            firstNameEditText.applyEditTextCustomization(paymentUIConfiguration)
            lastNameEditText.applyEditTextCustomization(paymentUIConfiguration)
            countryText.applyFakeEditTextCustomization(paymentUIConfiguration)
            ibanNumberEditText.applyEditTextCustomization(paymentUIConfiguration)
            saveButton.applyCustomization(paymentUIConfiguration)
            sepaScreenMainLayout.applyBackgroundCustomization(paymentUIConfiguration)
            sepaScreenCellLayout.applyCellBackgroundCustomization(paymentUIConfiguration)

            firstNameEditText.showKeyboardAndFocus()
        }

        disposables += Observables.combineLatest(
            firstNameTextChangedSubject,
            lastNameTextChangedSubject,
            ibanTextChangedSubject,
            countrySubject,
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

        countryText.onTextChanged { countrySubject.onNext(it.toString().trim()) }

        countryText.setOnClickListener {
            startActivityForResult(Intent(context, CountryChooserActivity::class.java)
                .putExtra(CountryChooserActivity.CURRENT_LOCATION_ENABLE_EXTRA, true)
                .putExtra(CountryChooserActivity.CURRENT_LOCATION_CUSTOM_EXTRA, suggestedCountry.country), COUNTRY_REQUEST_CODE)
        }

        saveButton.setOnClickListener {
            viewState?.let {
                val dataMap: MutableMap<String, String> = mutableMapOf()
                dataMap[BillingData.ADDITIONAL_DATA_FIRST_NAME] = it.firstName
                dataMap[BillingData.ADDITIONAL_DATA_LAST_NAME] = it.lastName
                dataMap[SepaData.IBAN] = it.iban
                dataMap[BillingData.ADDITIONAL_DATA_COUNTRY] = it.country
                uiComponentHandler.submitData(dataMap)
            }
        }

        countryText.text = suggestedCountry.displayCountry

        bsPayoneSepaEntrySwipeRefresh.isEnabled = false

        disposables += uiComponentHandler.getResultObservable().subscribe {
            when (it) {
                is UiRequestHandler.DataEntryResult.Success -> {
                    bsPayoneSepaEntrySwipeRefresh.isRefreshing = false
                }
                is UiRequestHandler.DataEntryResult.Processing -> {
                    bsPayoneSepaEntrySwipeRefresh.isRefreshing = true
                }
                is UiRequestHandler.DataEntryResult.Failure -> {
                    SnackBarExtensions {
                        bsPayoneSepaEntrySwipeRefresh.isRefreshing = false
                        it.throwable.getErrorSnackBar(sepaScreenMainLayout).show()
                    }
                }
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
        success = validateCountry(state.country).success && success
        saveButton.isEnabled = success
        CustomizationExtensions {
            saveButton.applyCustomization(paymentUIConfiguration)
        }
    }

    private fun validateFirstNameAndUpdateUI(name: String, isDelayed: Boolean): Boolean {
        val validationResult = validateName(name)
        if (!validationResult.success) {
            if (isDelayed) {
                stopTimer()
                startTimer(firstNameEditText, errorSepaFirstName, validationResult)
            } else {
                showError(firstNameEditText, errorSepaFirstName, validationResult)
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
                startTimer(lastNameEditText, errorSepaLastName, validationResult)
            } else {
                showError(lastNameEditText, errorSepaLastName, validationResult)
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
                startTimer(ibanNumberEditText, errorIban, validationResult)
            } else {
                showError(ibanNumberEditText, errorIban, validationResult)
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

    private fun validateCountry(country: String): ValidationResult {
        return ValidationResult(success = country.isNotEmpty())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == COUNTRY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                data?.getParcelableExtra<Country>(CountryChooserActivity.SELECTED_COUNTRY)?.let {
                    countryText.text = it.displayName
                }
            }
        } catch (ex: Exception) {
            Toast.makeText(activity, ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer(sourceView: View, errorView: TextView, validationResult: ValidationResult) {
        waitTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Do Nothing
            }

            override fun onFinish() {
                showError(sourceView, errorView, validationResult)
            }
        }.start()
    }

    private fun showError(sourceView: View, errorView: TextView, validationResult: ValidationResult) {
        errorView.setText(validationResult.errorMessageResourceId)
        errorView.visibility = View.VISIBLE
        sourceView.setBackgroundResource(R.drawable.edit_text_frame_error)
    }

    private fun hideError(sourceView: View, errorView: TextView) {
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
        val iban: String = "",
        val country: String = ""
    )
}