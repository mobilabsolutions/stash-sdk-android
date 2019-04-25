package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.text.Editable
import android.text.TextWatcher
import androidx.annotation.DrawableRes
import com.mobilabsolutions.payment.android.R

class CardNumberTextWatcher(val cardIconChanged: (Int) -> Unit) : TextWatcher {

    // It can be a space or a hyphen
    private var delimiter: String = DEFAULT_DELIMITER

    private var changeLocation: Int = 0

    // Default to Visa, as most cards falls in this number grouping pattern
    private var groupingPattern: String = GroupingPattern.VISA_MASTERCARD.pattern

    override fun beforeTextChanged(sequence: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(sequence: CharSequence, start: Int, before: Int, count: Int) {
        changeLocation = start
    }

    override fun afterTextChanged(editable: Editable) {
        if (changeLocation <= 2 && editable.toString().length <= 2) {
            // Reset if user clears the entry and try to enter a new card
            cardIconChanged(R.drawable.ic_card_default)
        } else if (changeLocation <= 7 || changeLocation >= 16) {
            // Mostly we can identify the card type by first 4 digits, except for China UnionPay , 17, for 19 digit cards)
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
                    CardType.MAESTRO_13 -> GroupingPattern.MAESTRO_13.pattern
                    CardType.MAESTRO_15 -> GroupingPattern.MAESTRO_15.pattern
                    CardType.UNIONPAY_19 -> GroupingPattern.UNIONPAY_19.pattern
                    else -> GroupingPattern.VISA_MASTERCARD.pattern
                })
                cardIconChanged(matchingPattern.resource)
                break
            }
        }
    }

    private fun applyPattern(editable: Editable) {
        val currentString = editable.toString()

        // Remove old grouping & apply new grouping, this covers any editing in between
        var processedString = editable.toString().replace("\\D".toRegex(), "")

        Regex(groupingPattern).find(processedString)?.let {
            processedString = it.destructured.toList().joinToString(separator = delimiter).trim()

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

        VISA_MASTERCARD("(\\d{4})(\\d{0,4})(\\d{0,4})(\\d{0,4})(\\d{0,3})"),    // 4-4-4-4(-3)

        AMEX("(\\d{4})(\\d{0,6})(\\d{0,5})"),                                   // 4-6-5

        DINERS("(\\d{4})(\\d{0,6})(\\d{0,4})"),                                 // 4-6-4

        MAESTRO_13("(\\d{4})(\\d{0,4})(\\d{0,5})"),                             // 4-4-5

        MAESTRO_15("(\\d{4})(\\d{0,6})(\\d{0,5})"),                             // 4-6-5

        UNIONPAY_19("(\\d{6})(\\d{0,13})")                                      // 6-13
    }

    private enum class CardType(val regex: Regex, @DrawableRes val resource: Int) {

        JBC(Regex("^(?:2131|1800|35[0-9]{3})[0-9]{3,}$"), R.drawable.ic_card_jcb),

        AMEX(Regex("^3[47][0-9]{1,13}$"), R.drawable.ic_card_amex),

        DINERS(Regex("^3(?:0[0-5]|[68][0-9])[0-9]{2,}$"), R.drawable.ic_card_diners_club),

        VISA(Regex("^4[0-9]{2,12}(?:[0-9]{3})?$"), R.drawable.ic_card_visa),

        MAESTRO_13(Regex("^50[0-9]{1,11}$"), R.drawable.ic_card_maestro),

        MAESTRO_15(Regex("^5[68][0-9]{1,12}$"), R.drawable.ic_card_maestro),

        MASTER_CARD(Regex("^5[1-5][0-9]{1,14}$"), R.drawable.ic_card_master),

        // Conflicts with MASTER_CARD, need a way (number range) distinguish. Also, applicable only for US Cards
        // DINERS_US(Regex("^5[45][0-9]{1,14}$"), R.drawable.ic_card_diners_club),

        DISCOVER(Regex("^6(?:011|5[0-9]{2})[0-9]{3,}$"), R.drawable.ic_card_discover),

        UNIONPAY_16(Regex("^62[0-9]{1,14}$"), R.drawable.ic_card_union_pay),

        UNIONPAY_19(Regex("^62[0-9]{15,17}$"), R.drawable.ic_card_union_pay),

        MAESTRO(Regex("^6[0-9]{1,18}$"), R.drawable.ic_card_maestro)

        // Carte Bleue, not implemented, as no known number range to distinguish
    }
}