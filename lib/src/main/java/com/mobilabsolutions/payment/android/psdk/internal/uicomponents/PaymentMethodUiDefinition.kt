package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import com.mobilabsolutions.payment.android.psdk.PaymentMethodType

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class PaymentMethodUiDefinition(
        val paymentMethodName : String,
        val paymentMethodType : PaymentMethodType,
        val uiDetailList : List<UiDetail>

)

interface ValidationRules {
    fun verify(data : String) : Pair<Boolean, String>
}

data class UiDetail(
        val identifier : String,
        val type : UiDetailType,
        val title : String,
        val hint : String,
        val validationRules: ValidationRules
)

enum class UiDetailType {
    NAME, ADDRESS, NUMBER, DATE
}

