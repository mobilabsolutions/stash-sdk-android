package com.mobilabsolutions.payment.sample.data.repositories.paymentmethod

import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Singleton
class PaymentMethodRepositoryImpl @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val localPaymentMethodStore: LocalPaymentMethodStore
) : PaymentMethodRepository {
    init {
        GlobalScope.launch(dispatchers.io) {
            localPaymentMethodStore.insertSampleData()
        }
    }

    override fun observePaymentMethods() = localPaymentMethodStore.observePaymentMethods()

    suspend fun addPaymentMethod() = coroutineScope {
    }

    override suspend fun deletePaymentMethod(paymentMethod: PaymentMethod) {
        localPaymentMethodStore.deletePaymentMethod(paymentMethod)
    }
}