package com.mobilabsolutions.payment.android.psdk.model

import org.iban4j.Iban

/**
 * This class models the data needed to register sepa account as a payment alias
 * @author [Ugi](ugi@mobilabsolutions.com)
 */
class SepaData(
        var bic: String = "",
        iban: String = "",
        val holder : String

) {
    var iban: String = iban
        set(iban) {
            field = iban
            parsedIban = Iban.valueOf(iban)
        }
    @Transient
    private var parsedIban: Iban? = null

    val accountNumberFromIban: String
        get() = parsedIban!!.accountNumber

    val bankCodeFromIban: String
        get() = parsedIban!!.bankCode

    val bankNumberFromIban: String
        get() = parsedIban!!.bban

    init {
        parsedIban = Iban.valueOf(iban)
    }
}
