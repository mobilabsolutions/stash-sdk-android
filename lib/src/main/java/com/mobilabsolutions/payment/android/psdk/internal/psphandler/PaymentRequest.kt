package com.mobilabsolutions.payment.android.psdk.internal.psphandler

import com.mobilabsolutions.payment.android.psdk.model.PaymentData

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface PaymentRequest {
    fun getStandardizedPaymentData() : StandardizedData

    fun getAdditionalPaymentData() : AdditionalPaymentData

}

interface StandardizedPaymentData {
    fun getPaymentData() : PaymentData
    fun getPaymentAlias() : String
}

/**
 *
 */
interface AdditionalPaymentData {

}