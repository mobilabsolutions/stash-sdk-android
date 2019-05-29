package com.mobilabsolutions.payment.sample.data.repositories.paymentmethod

import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.network.request.AuthorizePaymentRequest
import com.mobilabsolutions.payment.sample.network.response.AuthorizePaymentResponse
import io.reactivex.Observable
import io.reactivex.Single

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
interface PaymentMethodRepository {
    fun observePaymentMethods(): Observable<List<PaymentMethod>>
    suspend fun updatePaymentMethods(userId: String)
    suspend fun deletePaymentMethod(paymentMethod: PaymentMethod)
    suspend fun addPaymentMethod(userId: String, aliasId: String, paymentMethod: PaymentMethod)
    suspend fun authorizePayment(authorizePaymentRequest: AuthorizePaymentRequest): Single<AuthorizePaymentResponse>
}