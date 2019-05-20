package com.mobilabsolutions.payment.android.psdk.integration.adyen.uicomponents

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mobilabsolutions.payment.android.psdk.CustomizationExtensions
import com.mobilabsolutions.payment.android.psdk.CustomizationPreference
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import com.mobilabsolutions.payment.android.psdk.integration.adyen.AdyenIntegration
import com.mobilabsolutions.payment.android.psdk.integration.adyen.R
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.CardNumberTextWatcher
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.CreditCardDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.MonthYearPicker
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PersonalDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.getContentOnFocusLost
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.getContentsAsString
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.back
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.cvvTitleTextView
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.countryText
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.countryTitleTextView
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.creditCardNumberEditText
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.creditCardNumberTitleTextView
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.creditCardScreenCellLayout
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.creditCardScreenMainLayout
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.creditCardScreenTitle
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.cvvEditText
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.errorCreditCardNumber
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.expirationDateTextView
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.expirationDateTitleTextView
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.firstNameEditText
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.firstNameTitleTextView
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.lastNameEditText
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.lastNameTitleTextView
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.saveButton
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class AdyenCreditCardDataEntryFragment : Fragment() {

    @Inject
    lateinit var uiComponentHandler: UiComponentHandler

    @Inject
    lateinit var creditCardDataValidator: CreditCardDataValidator

    @Inject
    lateinit var personalDataValidator: PersonalDataValidator

    @Inject
    lateinit var uiCustomizationManager: UiCustomizationManager

    lateinit var customizationPreference: CustomizationPreference

    private val disposables = CompositeDisposable()
    private val firstNameSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val lastNameSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val ccNumberSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val expDateSubject: BehaviorSubject<LocalDate> = BehaviorSubject.create()
    private val ccvSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private var viewState: CreditCardDataEntryViewState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AdyenIntegration.integration?.adyenIntegrationComponent?.inject(this)
        Timber.d("Created")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.adyen_credit_card_data_entry_fragment, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        countryText.visibility = View.GONE
        countryTitleTextView.visibility = View.GONE

        disposables += Observables.combineLatest(
            firstNameSubject,
            lastNameSubject,
            ccNumberSubject,
            expDateSubject,
            ccvSubject,
            ::CreditCardDataEntryViewState)
            .subscribe(this::onViewState)

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

        disposables += ccNumberSubject
            .doOnNext {
                validateCreditCardNumber(it)
            }
            .subscribe()

        disposables += expDateSubject
            .doOnNext {
                validateExpirationDate(it)
            }
            .subscribe()

        disposables += ccvSubject
            .doOnNext {
                validateCvv(it)
            }
            .subscribe()

        customizationPreference = uiCustomizationManager.getCustomizationPreferences()

        CustomizationExtensions {

            creditCardScreenTitle.applyTextCustomization(customizationPreference)
            firstNameTitleTextView.applyTextCustomization(customizationPreference)
            lastNameTitleTextView.applyTextCustomization(customizationPreference)
            creditCardNumberTitleTextView.applyTextCustomization(customizationPreference)
            expirationDateTitleTextView.applyTextCustomization(customizationPreference)
            countryTitleTextView.applyTextCustomization(customizationPreference)
            cvvTitleTextView.applyTextCustomization(customizationPreference)

            firstNameEditText.applyEditTextCustomization(customizationPreference)
            lastNameEditText.applyEditTextCustomization(customizationPreference)
            creditCardNumberEditText.applyEditTextCustomization(customizationPreference)
            cvvEditText.applyEditTextCustomization(customizationPreference)
            expirationDateTextView.applyFakeEditTextCustomization(customizationPreference)
            countryText.applyFakeEditTextCustomization(customizationPreference)
            creditCardScreenMainLayout.applyBackgroundCustomization(customizationPreference)
            creditCardScreenCellLayout.applyCellBackgroundCustomization(customizationPreference)

            firstNameEditText.showKeyboardAndFocus()
        }
        firstNameEditText.getContentOnFocusLost { firstNameSubject.onNext(it.trim()) }
        lastNameEditText.getContentOnFocusLost { lastNameSubject.onNext(it.trim()) }
        creditCardNumberEditText.getContentOnFocusLost { ccNumberSubject.onNext(it.replace("\\D".toRegex(), "")) }
        cvvEditText.getContentOnFocusLost { ccvSubject.onNext(it.trim()) }

        countryText.setOnClickListener {
            Timber.d("Country selector")
        }

        creditCardNumberEditText.addTextChangedListener(CardNumberTextWatcher { resourceId ->
            creditCardNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, resourceId, 0)
        })

        saveButton.setOnClickListener {
            viewState?.let {
                val dataMap: MutableMap<String, String> = mutableMapOf()
                dataMap[BillingData.FIRST_NAME] = it.firstName
                dataMap[BillingData.LAST_NAME] = it.lastName
                dataMap[CreditCardData.CREDIT_CARD_NUMBER] = it.ccNumber
                dataMap[CreditCardData.CVV] = it.ccv
                dataMap[CreditCardData.EXPIRY_DATE] = expirationDateTextView.getContentsAsString()
                uiComponentHandler.dataSubject.onNext(dataMap)
            }
        }

        expirationDateTextView.setOnClickListener {
            val monthYearPicker = MonthYearPicker(requireContext(), customizationPreference = customizationPreference) {
                val selectedExpiry = LocalDate.of(it.second, it.first, 1)
                expDateSubject.onNext(selectedExpiry)
                val expDate = selectedExpiry.format(DateTimeFormatter.ofPattern("MM/yy"))
                expirationDateTextView.text = expDate
            }
            monthYearPicker.show()
        }

        back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun onViewState(state: CreditCardDataEntryViewState) {
        this.viewState = state
        var success = true
        success = validateFirstName(state.firstName) && success
        success = validateLastName(state.lastName) && success
        success = validateCreditCardNumber(state.ccNumber) && success
        success = validateCvv(state.ccv) && success
        success = validateExpirationDate(state.expDate) && success

        saveButton.isEnabled = success
        CustomizationExtensions {
            saveButton.applyCustomization(customizationPreference)
        }
    }

    private fun EditText.customError(message: String) {
        if (this.error == null) {
            this.setError(message, ContextCompat.getDrawable(requireContext(), R.drawable.empty_drawable))
            CustomizationExtensions {
                this@customError.applyEditTextCustomization(customizationPreference)
            }
        }
    }

    private fun EditText.clearError() {
        this.error = null
        CustomizationExtensions {
            this@clearError.applyEditTextCustomization(customizationPreference)
        }
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

    private fun validateCreditCardNumber(number: String): Boolean {
        val validationResult = creditCardDataValidator.validateCreditCardNumber(number)
        if (!validationResult.success) {
            errorCreditCardNumber.visibility = View.VISIBLE
            creditCardNumberEditText.setBackgroundResource(R.drawable.edit_text_frame_error)
        } else {
            creditCardNumberEditText.setBackgroundResource(R.drawable.edit_text_frame)
            errorCreditCardNumber.visibility = View.GONE
        }
        return validationResult.success
    }

    private fun validateCountry(country: String): Boolean {
        return if (country.isNotEmpty()) {
            countryText.error = null
            CustomizationExtensions {
                countryText.applyFakeEditTextCustomization(customizationPreference)
            }
            true
        } else {
            countryText.setError(getString(R.string.validation_error_missing_country), ContextCompat.getDrawable(requireContext(), R.drawable.empty_drawable))
            false
        }
    }

    private fun validateCvv(cvv: String): Boolean {
        val validationResult = creditCardDataValidator.validateCvv(cvv)
        if (!validationResult.success) {
            cvvEditText.customError(getString(validationResult.errorMessageResourceId))
        } else {
            cvvEditText.clearError()
        }
        return validationResult.success
    }

    private fun validateExpirationDate(expiryDate: LocalDate?): Boolean {
        return if (expiryDate == null) {
            countryText.error = null
            CustomizationExtensions {
                countryText.applyFakeEditTextCustomization(customizationPreference)
            }
            false
        } else {
            val validationResult = creditCardDataValidator.validateExpiry(expiryDate)
            if (!validationResult.success) {
                countryText.setError(getString(validationResult.errorMessageResourceId), ContextCompat.getDrawable(requireContext(), R.drawable.empty_drawable))
            } else {
                countryText.error = null
                CustomizationExtensions {
                    countryText.applyFakeEditTextCustomization(customizationPreference)
                }
            }
            validationResult.success
        }
    }

    data class CreditCardDataEntryViewState(
        val firstName: String = "",
        val lastName: String = "",
        val ccNumber: String = "",
        val expDate: LocalDate? = null,
        val ccv: String = ""
    )
}

inline fun TextView.onTextChanged(crossinline body: (text: CharSequence) -> Unit): TextWatcher {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = body(s)
        override fun afterTextChanged(s: Editable?) = Unit
    }
    addTextChangedListener(watcher)
    return watcher
}