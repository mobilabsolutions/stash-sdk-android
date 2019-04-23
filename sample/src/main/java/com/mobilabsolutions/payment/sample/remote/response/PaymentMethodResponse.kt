package com.mobilabsolutions.payment.sample.remote.response

import com.mobilabsolutions.payment.sample.remote.data.PaymentMethodData

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-04-2019.
 */
data class PaymentMethodResponse(
    val paymentMethods: List<PaymentMethodData>
)