/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk.model

/**
 * This class models the data needed to execute a payment
 *
 * @author [Ugi](ugi@mobilabsolutions.com)
 */
data class PaymentData(
    var customerId: String = "",
    var amount: Int = 0,
    var currency: String = "",
    var reason: String = ""
)
