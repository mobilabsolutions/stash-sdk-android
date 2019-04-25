package com.mobilabsolutions.payment.android.psdk.integration.bspayone.uicomponents

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.R
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.CreditCardDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PersonalDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.getContentsAsString
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.whiteelephant.monthpicker.MonthPickerDialog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class CreditCardDataEntryFragment : Fragment() {

    @Inject
    lateinit var uiComponentHandler: UiComponentHandler

    @Inject
    lateinit var creditCardDataValidator: CreditCardDataValidator

    @Inject
    lateinit var personalDataValidator: PersonalDataValidator

    private val disposables = CompositeDisposable()
    private val firstNameSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val lastNameSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val ccNumberSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val expDateSubject: BehaviorSubject<LocalDate> = BehaviorSubject.create()
    private val ccvSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val countrySubject: BehaviorSubject<String> = BehaviorSubject.createDefault("Germany")
    private var viewState: CreditCardDataEntryViewState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BsPayoneIntegration.integration?.bsPayoneIntegrationComponent?.inject(this)
        Timber.d("Created")
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.credit_card_data_entry_fragment, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disposables += Observables.combineLatest(
                firstNameSubject,
                lastNameSubject,
                ccNumberSubject,
                expDateSubject,
                ccvSubject,
                countrySubject,
                ::CreditCardDataEntryViewState)
                .subscribe(this::onViewState)

        firstNameEditText.onTextChanged { firstNameSubject.onNext(it.toString().trim()) }
        lastNameEditText.onTextChanged { lastNameSubject.onNext(it.toString().trim()) }
        creditCardNumberEditText.onTextChanged { ccNumberSubject.onNext(it.toString().trim()) }
        ccvEditText.onTextChanged { ccvSubject.onNext(it.toString().trim()) }
        countryText.onTextChanged { countrySubject.onNext(it.toString().trim()) }

        countryText.setOnClickListener {
            Timber.d("Country selector")
        }

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
            val today = LocalDate.now()
            val monthYearPicker = MonthPickerDialog.Builder(
                    requireActivity(),
                    MonthPickerDialog.OnDateSetListener { selectedMonth, selectedYear ->
                        val selectedExpiry = LocalDate.of(selectedYear, selectedMonth, 1)
                        expDateSubject.onNext(LocalDate.of(selectedYear, selectedMonth, 1))
                        val expDate = selectedExpiry.format(DateTimeFormatter.ofPattern("MM/yy"))
                        expirationDateTextView.text = expDate
                    },
                    today.year, today.monthValue + 1
            )
            monthYearPicker
                    .setMinMonth(today.monthValue)
                    .setMinYear(today.year)
                    .setYearRange(today.year, today.year + 20)
                    .build()
                    .show()
        }
    }

    private fun onViewState(state: CreditCardDataEntryViewState) {
        this.viewState = state
        var success = true
        success = validateFirstName(state.firstName) && success
        success = validateLastName(state.lastName) && success
        success = validateCreditCardNumber(state.ccNumber) && success
        success = validateCvv(state.ccv) && success
        success = validateCountry(state.country) && success
        success = validateExpirationDate(state.expDate) && success

        saveButton.isEnabled = success
    }

    private fun TextView.customError(message: String) {
        if (this.error == null) {
            this.setBackgroundResource(R.drawable.edit_text_frame_error)
            this.setError(message, ContextCompat.getDrawable(requireContext(), R.drawable.empty_drawable))
            this.invalidate()
        }
    }

    private fun TextView.clearError() {
        this.setBackgroundResource(R.drawable.edit_text_frame)
        this.error = null
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
        return if (!country.isEmpty()) {
            countryText.clearError()
            true
        } else {
            countryText.customError(getString(R.string.validation_error_missing_country))
            false
        }
    }

    private fun validateCvv(cvv: String): Boolean {
        val validationResult = creditCardDataValidator.validateCvv(cvv)
        if (!validationResult.success) {
            ccvEditText.customError(getString(validationResult.errorMessageResourceId))
        } else {
            ccvEditText.clearError()
        }
        return validationResult.success
    }

    private fun validateExpirationDate(expiryDate: LocalDate?): Boolean {
        return if (expiryDate == null) {
            expirationDateTextView.customError(getString(R.string.credit_card_data_expiry_validation_error))
            false
        } else {
            val validationResult = creditCardDataValidator.validateExpiry(expiryDate)
            if (!validationResult.success) {
                expirationDateTextView.customError(getString(validationResult.errorMessageResourceId))
            } else {
                expirationDateTextView.clearError()
            }
            validationResult.success
        }
    }

    data class CreditCardDataEntryViewState(
        val firstName: String = "",
        val lastName: String = "",
        val ccNumber: String = "",
        val expDate: LocalDate? = null,
        val ccv: String = "",
        val country: String = "Germany"
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