/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.paymentmethod

import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.network.request.AuthorizePaymentRequest
import io.reactivex.Observable

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
interface PaymentMethodRepository {
    fun observePaymentMethods(): Observable<List<PaymentMethod>>
    suspend fun updatePaymentMethods(userId: String)
    suspend fun deletePaymentMethod(paymentMethod: PaymentMethod)
    suspend fun addPaymentMethod(userId: String, aliasId: String, paymentMethod: PaymentMethod)
    suspend fun authorizePayment(authorizePaymentRequest: AuthorizePaymentRequest)
}