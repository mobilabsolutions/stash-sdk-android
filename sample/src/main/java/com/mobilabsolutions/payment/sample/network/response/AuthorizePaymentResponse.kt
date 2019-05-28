package com.mobilabsolutions.payment.sample.network.response

data class AuthorizePaymentResponse(
    val additionalInfo: String,
    val amount: Int,
    val currency: String,
    val status: String,
    val transactionId: String
)