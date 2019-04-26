package com.mobilabsolutions.payment.sample.data.repositories.paymentmethod

import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import io.reactivex.Observable

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
interface PaymentMethodRepository {
    fun observePaymentMethods(): Observable<List<PaymentMethod>>
    suspend fun updatePaymentMethods(userId: String)
    suspend fun deletePaymentMethod(paymentMethod: PaymentMethod)
    suspend fun addPaymentMethod(userId: String, paymentMethod: PaymentMethod)
}