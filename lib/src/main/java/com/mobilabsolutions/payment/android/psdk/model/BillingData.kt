package com.mobilabsolutions.payment.android.psdk.model

import java.util.*

/**
 * This class models the billing data needed when registering
 * a credit card or a sepa direct debit method as a payment aliasId
 *
 * @author [Ugi](ugi@mobilabsolutions.com)
 */
//data class BillingData(var country: String, val firstName: String, val city: String, val lastName: String, val email: String, val address1: String, val address2: String, val zip: String)

class BillingData (
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var address1: String? = null,
    var address2: String? = null,
    var zip: String? = null,
    var city: String? = null,
    var country: String? = null,
    val languageId : String = Locale.getDefault().isO3Language
    ) {

    companion object {
        @JvmStatic
        fun fromEmail(emailValue : String) = BillingData(email = emailValue)
        @JvmStatic
        fun fromName(name : String) = BillingData(name.split(' ')[0], name.split(' ')[1]) //TODO well it's obvious this is just a placeholder

        @JvmStatic
        fun empty() = BillingData()




    }

    class Builder {

        fun build() = BillingData()

    }
}
