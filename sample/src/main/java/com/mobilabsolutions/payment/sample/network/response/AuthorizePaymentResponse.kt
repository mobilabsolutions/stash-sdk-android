package com.mobilabsolutions.payment.sample.network.response

data class AuthorizePaymentResponse(
    val additionalInfo: String,
    val amount: Int = 0,
    val currency: String = "EUR",
    val status: String = "FAILED",
    val transactionId: String = ""
)