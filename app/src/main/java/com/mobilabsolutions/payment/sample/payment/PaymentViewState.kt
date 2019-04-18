package com.mobilabsolutions.payment.sample.payment

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class PaymentViewStateZ(
    val amount: String,
    val currency: String,
    val reason: String,

    val paymentMethods: List<String>,

    val enteringData: Boolean,
    val executingPayment: Boolean,
    val paymentSuccessful: Boolean,
    val paymentFailed: Boolean,
    val failureReason: String
)

sealed class PaymentViewState {
    object LoadingMethods : PaymentViewState()
    object ExecutingPayment : PaymentViewState()
    data class PaymentSuccess(val transactionId: String) : PaymentViewState()
    data class DataState(
        val paymentMethods: List<String>
    ) : PaymentViewState()
    data class ErrorState(val error: String) : PaymentViewState()
}