package com.mobilabsolutions.payment.sample.registration

import org.threeten.bp.LocalDate


/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class RegistrationViewState(
        val holderName : String = "",
        val address : String = "",
        val city : String = "",
        val country : String = "",
        val phone : String = "",
        val creditCardNumber : String = "",
        val cvv : String = "",
        val expiryDate : LocalDate = LocalDate.now(),
        val iban : String = "",
        val bic : String = "",
        val sepaSelected : Boolean = false,

        val enteringData : Boolean = true,
        val executingRegistration :  Boolean = false,
        val successfullRegistration : Pair<Boolean, String> = Pair(false, ""),
        val registrationFailed : Boolean = false,
        val failureReason : String = ""

)