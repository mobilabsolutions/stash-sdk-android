package com.mobilabsolutions.payment.sample.data.repositories.paymentmethod

import com.mobilabsolutions.payment.sample.data.entities.ErrorResult
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.data.entities.Success
import com.mobilabsolutions.payment.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Singleton
class PaymentMethodRepositoryImpl @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val localPaymentMethodStore: LocalPaymentMethodStore,
    private val paymentMethodDataSource: PaymentMethodDataSource
) : PaymentMethodRepository {
    override fun observePaymentMethods() = localPaymentMethodStore.observePaymentMethods()

    suspend fun addPaymentMethod() = coroutineScope {

    }

    override suspend fun deletePaymentMethod(paymentMethod: PaymentMethod) {
        localPaymentMethodStore.deletePaymentMethod(paymentMethod)
    }

    suspend fun refresh() = coroutineScope {
        val remoteJob = async(dispatchers.io) { paymentMethodDataSource.getPaymentMethods() }
        val result = remoteJob.await()
        when (result) {
            is Success -> localPaymentMethodStore.savePaymentMethod(result.data)
            is ErrorResult -> throw result.exception
        }
    }
}