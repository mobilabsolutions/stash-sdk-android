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
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.CardNumberTextWatcher
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.CreditCardDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.MonthYearPicker
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PersonalDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.ValidationResult
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.getContentOnFocusLost
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.getContentsAsString
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.observeText
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.util.CountryDetectorUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.back
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.countryText
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.countryTitleTextView
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.creditCardNumberEditText
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.creditCardNumberTitleTextView
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.creditCardScreenCellLayout
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.creditCardScreenMainLayout
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.creditCardScreenTitle
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.cvvEditText
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.cvvTitleTextView
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.errorCreditCardCVV
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.errorCreditCardFirstName
import kotlinx.android.synthetic.main.adyen_credit_card_data_entry_fragment.errorCreditCardLastName
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

    private lateinit var customizationPreference: CustomizationPreference

    private val disposables = CompositeDisposable()

    private val firstNameLostFocusSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val firstNameTextChangedSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private val lastNameLostFocusSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val lastNameTextChangedSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private val cardNumberLostFocusSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val cardNumberTextChangedSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private val expirationDateSubject: BehaviorSubject<LocalDate> = BehaviorSubject.create()

    private val ccvLostFocusSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val ccvTextChangedSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private var viewState: CreditCardDataEntryViewState? = null

    private var waitTimer: CountDownTimer? = null

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
            firstNameTextChangedSubject,
            lastNameTextChangedSubject,
            cardNumberTextChangedSubject,
            expirationDateSubject,
            ccvTextChangedSubject,
            ::CreditCardDataEntryViewState)
            .subscribe(this::onViewState)

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

        disposables += cardNumberLostFocusSubject
            .doOnNext {
                validateCardNumberAndUpdateUI(it, false)
            }
            .subscribe()

        disposables += cardNumberTextChangedSubject
            .doOnNext {
                validateCardNumberAndUpdateUI(it, true)
            }
            .subscribe()

        disposables += expirationDateSubject
            .doOnNext {
                validateExpirationDateAndUpdateUI(it)
            }
            .subscribe()

        disposables += ccvLostFocusSubject
            .doOnNext {
                validateCvvAndUpdateUI(it, false)
            }
            .subscribe()

        disposables += ccvTextChangedSubject
            .doOnNext {
                validateCvvAndUpdateUI(it, true)
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

        firstNameEditText.getContentOnFocusLost { firstNameLostFocusSubject.onNext(it.trim()) }
        firstNameEditText.observeText { firstNameTextChangedSubject.onNext(it.trim()) }

        lastNameEditText.getContentOnFocusLost { lastNameLostFocusSubject.onNext(it.trim()) }
        lastNameEditText.observeText { lastNameTextChangedSubject.onNext(it.trim()) }

        creditCardNumberEditText.getContentOnFocusLost { cardNumberLostFocusSubject.onNext(it.replace("\\D".toRegex(), "")) }
        creditCardNumberEditText.observeText { cardNumberTextChangedSubject.onNext(it.replace("\\D".toRegex(), "")) }

        cvvEditText.getContentOnFocusLost { ccvLostFocusSubject.onNext(it.trim()) }
        cvvEditText.observeText { ccvTextChangedSubject.onNext(it.trim()) }

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
                dataMap[CreditCardData.CREDIT_CARD_NUMBER] = it.cardNumber
                dataMap[CreditCardData.CVV] = it.cvv
                dataMap[CreditCardData.EXPIRY_DATE] = expirationDateTextView.getContentsAsString()
                uiComponentHandler.dataSubject.onNext(dataMap)
            }
        }

        expirationDateTextView.setOnClickListener {
            val monthYearPicker = MonthYearPicker(requireContext(), customizationPreference = customizationPreference) {
                val selectedExpiryWithoutLastDay = LocalDate.of(it.second, it.first, 1)
                val lastDay = selectedExpiryWithoutLastDay.month.length(selectedExpiryWithoutLastDay.isLeapYear)
                val selectedExpiry = LocalDate.of(it.second, it.first, lastDay)
                expirationDateSubject.onNext(selectedExpiry)
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
        success = validateName(state.firstName).success && success
        success = validateName(state.lastName).success && success
        success = validateCardNumber(state.cardNumber).success && success
        success = validateExpirationDate(state.expirationDate).success && success
        success = validateCVV(state.cvv).success && success
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
                startTimer(firstNameEditText, errorCreditCardFirstName)
            } else {
                showError(firstNameEditText, errorCreditCardFirstName)
            }
        } else {
            stopTimer()
            hideError(firstNameEditText, errorCreditCardFirstName)
        }
        return validationResult.success
    }

    private fun validateLastNameAndUpdateUI(name: String, isDelayed: Boolean): Boolean {
        val validationResult = validateName(name)
        if (!validationResult.success) {
            if (isDelayed) {
                stopTimer()
                startTimer(lastNameEditText, errorCreditCardLastName)
            } else {
                showError(lastNameEditText, errorCreditCardLastName)
            }
        } else {
            stopTimer()
            hideError(lastNameEditText, errorCreditCardLastName)
        }
        return validationResult.success
    }

    private fun validateName(name: String): ValidationResult {
        return personalDataValidator.validateName(name)
    }

    private fun validateCardNumberAndUpdateUI(number: String, isDelayed: Boolean): Boolean {
        val validationResult = validateCardNumber(number)
        if (!validationResult.success) {
            if (isDelayed) {
                stopTimer()
                startTimer(creditCardNumberEditText, errorCreditCardNumber)
            } else {
                showError(creditCardNumberEditText, errorCreditCardNumber)
            }
        } else {
            stopTimer()
            hideError(creditCardNumberEditText, errorCreditCardNumber)
        }
        return validationResult.success
    }

    private fun validateCardNumber(number: String): ValidationResult {
        return creditCardDataValidator.validateCreditCardNumber(number)
    }

    private fun validateCvvAndUpdateUI(cvv: String, isDelayed: Boolean): Boolean {
        val validationResult = validateCVV(cvv)
        if (!validationResult.success) {
            if (isDelayed) {
                stopTimer()
                startTimer(cvvEditText, errorCreditCardCVV)
            } else {
                showError(cvvEditText, errorCreditCardCVV)
            }
        } else {
            stopTimer()
            hideError(cvvEditText, errorCreditCardCVV)
        }
        return validationResult.success
    }

    private fun validateCVV(cvv: String): ValidationResult {
        return creditCardDataValidator.validateCvv(cvv)
    }

    private fun validateExpirationDateAndUpdateUI(expiryDate: LocalDate?): Boolean {
        val validationResult = validateExpirationDate(expiryDate)
        if (!validationResult.success) {
            // Do Nothing
        } else {
            CustomizationExtensions {
                countryText.applyFakeEditTextCustomization(customizationPreference)
            }
        }
        return validationResult.success
    }

    private fun validateExpirationDate(expiryDate: LocalDate?): ValidationResult {
        expiryDate?.let {
            return creditCardDataValidator.validateExpiry(expiryDate)
        }
        return ValidationResult(success = false)
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

    data class CreditCardDataEntryViewState(
        val firstName: String = "",
        val lastName: String = "",
        val cardNumber: String = "",
        val expirationDate: LocalDate? = null,
        val cvv: String = ""
    )
}
