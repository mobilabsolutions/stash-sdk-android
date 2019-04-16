package com.mobilabsolutions.payment.sample.data.repositories.paymentmethod

import com.mobilabsolutions.payment.sample.data.DatabaseTransactionRunner
import com.mobilabsolutions.payment.sample.data.daos.EntityInserter
import com.mobilabsolutions.payment.sample.data.daos.PaymentMethodDao
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class LocalPaymentMethodStore @Inject constructor(
        private val transactionRunner: DatabaseTransactionRunner,
        private val entityInserter: EntityInserter,
        private val paymentMethodDao: PaymentMethodDao
) {

    companion object {
        private val creditCard = PaymentMethod(
                id = 1,
                paymentMethodId = "payment_method_id_1",
                alias = "XXX",
                _type = "credit_card"
        )

        private val sepa = PaymentMethod(
                id = 2,
                paymentMethodId = "payment_method_id_2",
                alias = "DE12349790",
                _type = "sepa"
        )

        private val paypal = PaymentMethod(
                id = 3,
                paymentMethodId = "payment_method_id_3",
                alias = "maxmustermann@gmail.com",
                _type = "paypal"
        )
    }

    suspend fun insertSampleData() {
        if (paymentMethodDao.paymentMethodsCount() < 1) {
            paymentMethodDao.insertAll(listOf(creditCard, sepa, paypal))
        }
    }

    fun observePaymentMethods() = paymentMethodDao.entriesObservable()

    suspend fun savePaymentMethod() = transactionRunner {

    }

    suspend fun deletePaymentMethod(paymentMethod: PaymentMethod) = transactionRunner {
        paymentMethodDao.delete(paymentMethod)
    }
}