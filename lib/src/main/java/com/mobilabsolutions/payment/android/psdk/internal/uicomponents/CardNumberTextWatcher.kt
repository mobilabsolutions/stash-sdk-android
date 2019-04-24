package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.text.Editable
import android.text.TextWatcher

class CardNumberTextWatcher : TextWatcher {

    // It can be a space or a hyphen
    private var delimiter: String = DEFAULT_DELIMITER

    private var changeLocation: Int = 0

    // Default to Visa, as most cards falls in this number grouping pattern
    private var groupingPattern: String = GroupingPattern.VISA.pattern

    override fun beforeTextChanged(sequence: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(sequence: CharSequence, start: Int, before: Int, count: Int) {
        changeLocation = start
    }

    override fun afterTextChanged(editable: Editable) {
        // Mostly we can identify the card type by first 4 digits, except for China UnionPay , 17, for 19 digit cards)
        if (changeLocation <= 7 || changeLocation >= 16) {
            identify(editable)
        }
        applyPattern(editable)
    }

    private fun identify(editable: Editable) {
        val currentString = editable.toString().replace("\\D".toRegex(), "")
        for (matchingPattern in CardType.values()) {
            if (currentString.matches(matchingPattern.regex)) {
                groupingPattern = (when (matchingPattern) {
                    CardType.AMEX -> GroupingPattern.AMEX.pattern
                    CardType.DINERS -> GroupingPattern.DINERS.pattern
                    CardType.UNIONPAY_19 -> GroupingPattern.UNIONPAY_19.pattern
                    else -> GroupingPattern.VISA.pattern
                })
                break
            }
        }
    }

    private fun applyPattern(editable: Editable) {
        val currentString = editable.toString()

        // Remove old grouping & apply new grouping, this covers any editing in between
        var processedString = editable.toString().replace("\\D".toRegex(), "")

        Regex(groupingPattern).find(processedString)?.let {
            processedString = it.destructured.toList().joinToString(separator = " ").trim()

            // It's costly, don't apply it if they match
            if (currentString != processedString) {
                editable.replace(0, currentString.length, processedString)
            }
        }
    }

    companion object {
        const val DEFAULT_DELIMITER = " "

        @Suppress("UNUSED")
        const val HYPHEN_DELIMITER = "-"
    }

    /**
     * Ref : https://baymard.com/checkout-usability/credit-card-patterns
     */
    private enum class GroupingPattern(val pattern: String) {
        VISA("(\\d{4})(\\d{0,4})(\\d{0,4})(\\d{0,4})(\\d{0,3})"),   // 4-4-4-4(-3)
        AMEX("(\\d{4})(\\d{0,6})(\\d{0,5})"),                       // 4-6-5
        DINERS("(\\d{4})(\\d{0,6})(\\d{0,4})"),                     // 4-6-4
        UNIONPAY_19("(\\d{6})(\\d{0,13})")                          // 6-13
    }

    private enum class CardType(val regex: Regex) {
        JBC(Regex("^(?:2131|1800|35[0-9]{3})[0-9]{3,}$")),
        AMEX(Regex("^3[47][0-9]{1,13}$")),
        DINERS(Regex("^3(?:0[0-5]|[68][0-9])[0-9]{2,}$")),
        VISA(Regex("^4[0-9]{2,12}(?:[0-9]{3})?$")),
        DINERS_US(Regex("^5[45][0-9]{1,14}$")),
        MASTERCARD(Regex("^5[1-5][0-9]{1,14}$")),
        DISCOVER(Regex("^6(?:011|5[0-9]{2})[0-9]{3,}$")),
        UNIONPAY_16(Regex("^62[0-9]{1,14}$")),
        UNIONPAY_19(Regex("^62[0-9]{15,17}$"))
        //TODO: Add Maestro - 500000‑509999 (4-4-5) & 560000‑589999 (4-6-5)
    }
}