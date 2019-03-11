package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class PaymentMethodUiDefinition(
        val paymentMethodName : String,
        val paymentMethodType : PaymentMethodType,
        val uiDetailList : List<UiDetail>

)

data class UiDetail(
        val identifier : String,
        val type : UiDetailType,
        val title : String,
        val hint : String
)

enum class UiDetailType {
    NAME, ADDRESS, NUMBER
}

enum class PaymentMethodType {
    CREDITCARD, SEPA, PAYPAL
}