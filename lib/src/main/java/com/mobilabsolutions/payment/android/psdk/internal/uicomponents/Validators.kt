package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import com.mobilabsolutions.payment.android.R
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationScope
import org.apache.commons.validator.routines.CreditCardValidator
import org.iban4j.Iban
import org.threeten.bp.LocalDate
import javax.inject.Inject

/**
 * @author [Ugljesa Jovanovic](ugi@mobilabsolutions.com)
 */

data class ValidationResult(val success: Boolean, val errorMessageResourceId: Int = R.string.general_error)

@IntegrationScope
class CreditCardDataValidator @Inject constructor() {
    private val validator = CreditCardValidator()

    fun validateCreditCardNumber(number: String): ValidationResult {
        return when {
            number.isEmpty() -> ValidationResult(false, R.string.validation_error_empty)
            validator.isValid(number) -> ValidationResult(success = true)
            else -> ValidationResult(success = false, errorMessageResourceId = R.string.credit_card_data_number_validation_error)
        }
    }

    fun validateCvv(cvv: String): ValidationResult {
        return when {
            cvv.length in 3..4 -> ValidationResult(success = true)
            cvv.isEmpty() -> ValidationResult(false, R.string.validation_error_empty)
            else -> ValidationResult(success = false, errorMessageResourceId = R.string.credit_card_data_cvv_validation_error)
        }
    }

    fun validateExpiry(expiryDate: LocalDate): ValidationResult {
        return if (!expiryDate.isBefore(LocalDate.now())) {
            ValidationResult(success = true)
        } else {
            ValidationResult(success = false, errorMessageResourceId = R.string.credit_card_data_expiry_validation_error)
        }
    }
}

@IntegrationScope
class SepaDataValidator @Inject constructor() {
    fun validateIban(iban: String): ValidationResult {
        if (iban.isBlank()) {
            return ValidationResult(false, R.string.validation_error_empty)
        }
        try {
            Iban.valueOf(iban)
        } catch (exception: Exception) {
            return ValidationResult(false, R.string.sepa_data_entry_invalid_iban)
        }
        return ValidationResult(true)
    }
}

@IntegrationScope
class PersonalDataValidator @Inject constructor() {
    fun validateName(name: String): ValidationResult {
        if (name.isEmpty()) {
            return ValidationResult(false, R.string.validation_error_empty)
        }
        if (name.fold(false) { acc, char -> acc || char.isDigit() }) {
            return ValidationResult(false, R.string.validation_error_invalid_characters)
        }
        return ValidationResult(true)
    }
}