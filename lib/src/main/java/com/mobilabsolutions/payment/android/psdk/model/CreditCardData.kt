package com.mobilabsolutions.payment.android.psdk.model

import org.threeten.bp.LocalDate

/**
 * This class models data needed to register a credit card as a payment method
 * @author [Ugi](ugi@mobilabsolutions.com)
 */
class CreditCardData(val number: String, val expiryDate: LocalDate, val cvv: String, val holder: String, additionalData : Map<String, String> = emptyMap())

