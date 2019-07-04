/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.sample.data.repositories.paymentmethod

import com.mobilabsolutions.payment.sample.data.entities.ErrorResult
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.data.entities.Success
import com.mobilabsolutions.payment.sample.data.repositories.cart.LocalCartStore
import com.mobilabsolutions.payment.sample.network.request.AuthorizePaymentRequest
import com.mobilabsolutions.payment.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Singleton
class PaymentMethodRepositoryImpl @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val localPaymentMethodStore: LocalPaymentMethodStore,
    private val localCartStore: LocalCartStore,
    private val remotePaymentMethodDataSource: RemotePaymentMethodDataSource
) : PaymentMethodRepository {

    override fun observePaymentMethods() = localPaymentMethodStore.observePaymentMethods()

    override suspend fun updatePaymentMethods(userId: String) = supervisorScope {
        val remoteJob = async(dispatchers.io) { remotePaymentMethodDataSource.getPaymentMethods(userId) }
        when (val result = remoteJob.await()) {
            is Success -> localPaymentMethodStore.savePaymentMethodList(userId, result.data)
            is ErrorResult -> throw result.exception
        }
        Unit
    }

    override suspend fun addPaymentMethod(userId: String, aliasId: String, paymentMethod: PaymentMethod) = supervisorScope {
        val remoteJob = async(dispatchers.io) { remotePaymentMethodDataSource.addPaymentMethod(userId, aliasId, paymentMethod) }
        when (val result = remoteJob.await()) {
            is Success -> localPaymentMethodStore.savePaymentMethod(paymentMethod.copy(userId = userId), result.data.paymentMethodId)
            is ErrorResult -> throw result.exception
        }
        Unit
    }

    override suspend fun deletePaymentMethod(paymentMethod: PaymentMethod) = supervisorScope {
        val remoteJob = async(dispatchers.io) { remotePaymentMethodDataSource.deletePaymentMethod(paymentMethod.paymentMethodId) }
        when (val result = remoteJob.await()) {
            is Success -> localPaymentMethodStore.deletePaymentMethod(paymentMethod)
            is ErrorResult -> throw result.exception
        }
        Unit
    }

    override suspend fun authorizePayment(authorizePaymentRequest: AuthorizePaymentRequest) = supervisorScope {
        val localJob = async(dispatchers.io) { localCartStore.emptyCart() }
        val remoteJob = async(dispatchers.io) { remotePaymentMethodDataSource.authorizePayment(authorizePaymentRequest) }
        when (val result = remoteJob.await()) {
            is Success -> localJob.await()
            is ErrorResult -> throw result.exception
        }
        Unit
    }
}