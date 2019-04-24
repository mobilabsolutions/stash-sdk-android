package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.text.Editable
import android.text.TextWatcher

class CardNumberTextWatcher() : TextWatcher {

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
        // Mostly we can identify the card type by first 4 digits (except some Mastero Cards)
        if (changeLocation <= 4) {
            identify(editable)
        }
        applyPattern(editable)
    }

    private fun identify(editable: Editable) {
        val currentString = editable.toString()

        for (matchingPattern in CardType.values()) {
            if (currentString.matches(matchingPattern.regex)) {
                groupingPattern = (when (matchingPattern) {
                    CardType.AMEX -> GroupingPattern.AMEX.pattern
                    CardType.DINERS -> GroupingPattern.DINERS.pattern
                    else -> GroupingPattern.VISA.pattern
                })
                break
            }
        }
    }

    private fun applyPattern(editable: Editable) {
        val currentString = editable.toString()

        // Remove old grouping & apply new grouping, this covers any editing in between
        val processedString = editable.toString()
                .replace("\\D".toRegex(), "")
                .replace(groupingPattern.toRegex(), "$1$delimiter")

        // It's costly, don't do it if they match
        if (currentString != processedString) {
            editable.replace(0, currentString.length, processedString)
        }
    }

    companion object {
        const val DEFAULT_DELIMITER = " "

        @Suppress("UNUSED")
        const val HYPHEN_DELIMITER = "-"
    }

    private enum class GroupingPattern(val pattern: String) {
        VISA("(\\d{4})(?=\\d)"),            // 4-4-4-4(-3)
        AMEX("(\\d{4})(\\d{6})(\\d{5})"),   // 4-6-5
        DINERS("(\\d{4})(\\d{6})(\\d{4})")  // 4-6-4
    }

    private enum class CardType(val regex: Regex) {
        VISA(Regex("^4[0-9]{2,12}(?:[0-9]{3})?$")),
        MASTERCARD(Regex("^5[1-5][0-9]{1,14}$")),
        AMEX(Regex("^3[47][0-9]{1,13}$")),
        DINERS(Regex("^3(?:0[0-5]|[68][0-9])[0-9]{4,}$")),
        DISCOVER(Regex("^6(?:011|5[0-9]{2})[0-9]{3,}$")),
        JBC(Regex("^(?:2131|1800|35[0-9]{3})[0-9]{3,}$"))
    }
}