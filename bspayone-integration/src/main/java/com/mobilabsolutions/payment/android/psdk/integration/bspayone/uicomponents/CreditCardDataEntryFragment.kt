package com.mobilabsolutions.payment.android.psdk.integration.bspayone.uicomponents

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.R
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.CreditCardDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PersonalDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.getContentOnFocusLost
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.getContentsAsString
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

    lateinit var errorDrawable: Drawable
    lateinit var normalBacgroundDrawable: Drawable

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
        errorDrawable = resources.getDrawable(R.drawable.edit_text_frame_error)
        normalBacgroundDrawable = resources.getDrawable(R.drawable.edit_text_frame)
        firstNameEditText.getContentOnFocusLost {
            validateFirstName(it)
        }
        lastNameEditText.getContentOnFocusLost {
            validateLastName(it)
        }
        countryText.setOnClickListener {
            Timber.d("Country selector")
        }

        saveButton.setOnClickListener {
            var success = true
            success = validateFirstName(firstNameEditText.getContentsAsString()) && success
            success = validateLastName(lastNameEditText.getContentsAsString()) && success
            success =
                validateCreditCardNumber(creditCardNumberEditText.getContentsAsString()) && success
            success = validateCvv(ccvEditText.getContentsAsString()) && success
            success = validateCountry(countryText.text.toString()) && success
            success =
                validateExpirationDate(selectedExpiry) && success
            if (success) {
                val dataMap: MutableMap<String, String> = mutableMapOf()
                dataMap.put(BillingData.FIRST_NAME, firstNameEditText.getContentsAsString())
                dataMap.put(BillingData.LAST_NAME, lastNameEditText.getContentsAsString())

                dataMap.put(
                    CreditCardData.CREDIT_CARD_NUMBER,
                    creditCardNumberEditText.getContentsAsString()
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
                    expirationDateTextView.text = selectedExpiry!!.format(DateTimeFormatter.ofPattern("MM/yy"))
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

    fun validateFirstName(name: String): Boolean {
        val validationResult = personalDataValidator.validateName(name)
        if (!validationResult.success) {
            firstNameEditText.background = errorDrawable
            firstNameEditText.setError(getString(validationResult.errorMessageResourceId), null)
        } else {
            firstNameEditText.background = normalBacgroundDrawable
            firstNameEditText.setError(null, null)
        }
        return validationResult.success
    }

    fun validateLastName(name: String): Boolean {
        val validationResult = personalDataValidator.validateName(name)
        if (!validationResult.success) {
            lastNameEditText.background = errorDrawable
            lastNameEditText.setError(getString(validationResult.errorMessageResourceId), null)
        } else {
            lastNameEditText.background = normalBacgroundDrawable
            lastNameEditText.setError(null, null)
        }
        return validationResult.success
    }

    fun validateCreditCardNumber(number: String): Boolean {
        val validationResult = creditCardDataValidator.validateCreditCardNumber(number)
        if (!validationResult.success) {
            creditCardNumberEditText.background = errorDrawable
            creditCardNumberEditText.setError(
                getString(validationResult.errorMessageResourceId),
                null
            )
        } else {
            creditCardNumberEditText.background = normalBacgroundDrawable
            creditCardNumberEditText.setError(null, null)
        }
        return validationResult.success
    }

    fun validateCountry(country: String): Boolean {
        return if (!country.isEmpty()) {
            countryText.background = normalBacgroundDrawable
            countryText.setError(null, null)
            true
        } else {
            countryText.background = errorDrawable
            countryText.setError(getString(R.string.validation_error_missing_country), null)
            false
        }
    }

    fun validateCvv(cvv: String): Boolean {
        val validationResult = creditCardDataValidator.validateCvv(cvv)
        if (!validationResult.success) {
            ccvEditText.background = errorDrawable
            ccvEditText.setError(getString(validationResult.errorMessageResourceId), null)
        } else {
            ccvEditText.background = normalBacgroundDrawable
            ccvEditText.setError(null, null)
        }
        return validationResult.success
    }

    fun validateExpirationDate(expiryDate: LocalDate?): Boolean {
        if (expiryDate == null) {
            expirationDateTextView.background = errorDrawable
            expirationDateTextView.setError(
                getString(R.string.credit_card_data_expiry_validation_error),
                null
            )
            return false
        } else {
            val validationResult = creditCardDataValidator.validateExpiry(expiryDate)
            if (!validationResult.success) {
                expirationDateTextView.background = errorDrawable
                expirationDateTextView.setError(
                    getString(validationResult.errorMessageResourceId),
                    null
                )
            } else {
                expirationDateTextView.background = normalBacgroundDrawable
                expirationDateTextView.setError(null, null)
            }
            return validationResult.success
        }
    }
}