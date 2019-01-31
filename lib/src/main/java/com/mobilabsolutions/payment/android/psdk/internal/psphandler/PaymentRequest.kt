package com.mobilabsolutions.payment.android.psdk.internal.psphandler

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface PaymentRequest {
    fun getStandardizedPaymentData() : StandardizedData

    fun getAdditionalPaymentData() : AdditionalPaymentData

}

interface StandardizedPaymentData

interface AdditionalPaymentData