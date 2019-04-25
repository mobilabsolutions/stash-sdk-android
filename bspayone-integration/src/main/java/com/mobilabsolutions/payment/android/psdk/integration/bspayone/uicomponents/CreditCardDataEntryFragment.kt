package com.mobilabsolutions.payment.android.psdk.integration.bspayone.uicomponents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.R
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.*
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.whiteelephant.monthpicker.MonthPickerDialog
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

    var selectedExpiry: LocalDate? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.credit_card_data_entry_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firstNameEditText.getContentOnFocusLost {
            validateFirstName(it)
        }
        lastNameEditText.getContentOnFocusLost {
            validateLastName(it)
        }
        countryText.setOnClickListener {
            Timber.d("Country selector")
        }

        creditCardNumberEditText.addTextChangedListener(CardNumberTextWatcher { resourceId ->
            creditCardNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, resourceId, 0)
        })

        saveButton.setOnClickListener {
            var success = true
            success = validateFirstName(firstNameEditText.getContentsAsString()) && success
            success = validateLastName(lastNameEditText.getContentsAsString()) && success
            success = validateCreditCardNumber(creditCardNumberEditText.getContentAsSpacesRemovedString()) && success
            success = validateCvv(ccvEditText.getContentsAsString()) && success
            success = validateCountry(countryText.text.toString()) && success
            success = validateExpirationDate(selectedExpiry) && success
            if (success) {
                val dataMap: MutableMap<String, String> = mutableMapOf()
                dataMap.put(BillingData.FIRST_NAME, firstNameEditText.getContentsAsString())
                dataMap.put(BillingData.LAST_NAME, lastNameEditText.getContentsAsString())

                dataMap.put(
                        CreditCardData.CREDIT_CARD_NUMBER,
                        creditCardNumberEditText.getContentAsSpacesRemovedString()
                )
                dataMap.put(CreditCardData.CVV, ccvEditText.getContentsAsString())
                dataMap.put(
                        CreditCardData.EXPIRY_DATE,
                        expirationDateTextView.getContentsAsString()
                )
                uiComponentHandler.dataSubject.onNext(dataMap)
            }
        }

        expirationDateTextView.setOnClickListener {
            val today = LocalDate.now()
            val monthYearPicker = MonthPickerDialog.Builder(
                    activity,
                    MonthPickerDialog.OnDateSetListener { selectedMonth, selectedYear ->
                        selectedExpiry = LocalDate.of(selectedYear, selectedMonth, 1)
                        expirationDateTextView.text =
                                selectedExpiry!!.format(DateTimeFormatter.ofPattern("MM/yy"))
                        expirationDateTextView.clearError()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BsPayoneIntegration.integration?.bsPayoneIntegrationComponent?.inject(this)
        Timber.d("Created")
    }

    private fun TextView.customError(message: String) {
        if (this.error == null) {
            this.setBackgroundResource(R.drawable.edit_text_frame_error)
            this.setError(message, resources.getDrawable(R.drawable.empty_drawable))
            this.invalidate()
        }
    }

    private fun TextView.clearError() {
        this.setBackgroundResource(R.drawable.edit_text_frame)
        this.error = null
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

    fun validateCreditCardNumber(number: String): Boolean {
        val validationResult = creditCardDataValidator.validateCreditCardNumber(number)
        if (!validationResult.success) {

            creditCardNumberEditText.customError(getString(validationResult.errorMessageResourceId))
        } else {
            creditCardNumberEditText.clearError()
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

    fun validateCvv(cvv: String): Boolean {
        val validationResult = creditCardDataValidator.validateCvv(cvv)
        if (!validationResult.success) {
            ccvEditText.customError(getString(validationResult.errorMessageResourceId))
        } else {
            ccvEditText.clearError()
        }
        return validationResult.success
    }

    fun validateExpirationDate(expiryDate: LocalDate?): Boolean {
        if (expiryDate == null) {
            expirationDateTextView.customError(getString(R.string.credit_card_data_expiry_validation_error))
            return false
        } else {
            val validationResult = creditCardDataValidator.validateExpiry(expiryDate)
            if (!validationResult.success) {
                expirationDateTextView.customError(getString(validationResult.errorMessageResourceId))
            } else {
                expirationDateTextView.clearError()
            }
            return validationResult.success
        }
    }
}