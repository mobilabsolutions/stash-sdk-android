package com.mobilabsolutions.payment.sample.data.repositories.paymentmethod

import com.mobilabsolutions.payment.sample.data.entities.ErrorResult
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.data.entities.Success
import com.mobilabsolutions.payment.sample.network.request.AuthorizePaymentRequest
import com.mobilabsolutions.payment.sample.network.response.AuthorizePaymentResponse
import com.mobilabsolutions.payment.sample.util.AppCoroutineDispatchers
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Singleton
class PaymentMethodRepositoryImpl @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val localPaymentMethodStore: LocalPaymentMethodStore,
    private val remotePaymentMethodDataSource: RemotePaymentMethodDataSource
) : PaymentMethodRepository {

    override fun observePaymentMethods() = localPaymentMethodStore.observePaymentMethods()

    override suspend fun updatePaymentMethods(userId: String) = coroutineScope {
        val remoteJob = async(dispatchers.io) { remotePaymentMethodDataSource.getPaymentMethods(userId) }
        when (val result = remoteJob.await()) {
            is Success -> localPaymentMethodStore.savePaymentMethodList(userId, result.data)
            is ErrorResult -> Timber.e(result.exception) //TODO: Biju: Show Toast/SnackBar
        }
        Unit
    }

    override suspend fun addPaymentMethod(userId: String, aliasId: String, paymentMethod: PaymentMethod) = coroutineScope {
        val remoteJob = async(dispatchers.io) { remotePaymentMethodDataSource.addPaymentMethod(userId, aliasId, paymentMethod) }
        when (val result = remoteJob.await()) {
            is Success -> localPaymentMethodStore.savePaymentMethod(paymentMethod.copy(userId = userId), result.data.paymentMethodId)
            is ErrorResult -> Timber.e(result.exception) //TODO: Biju: Show Toast/SnackBar
        }
        Unit
    }

    override suspend fun deletePaymentMethod(paymentMethod: PaymentMethod) = coroutineScope {
        val remoteJob = async(dispatchers.io) { remotePaymentMethodDataSource.deletePaymentMethod(paymentMethod.paymentMethodId) }
        when (val result = remoteJob.await()) {
            is Success -> localPaymentMethodStore.deletePaymentMethod(paymentMethod)
            is ErrorResult -> Timber.e(result.exception) //TODO: Biju: Show Toast/SnackBar
        }
        Unit
    }

    override suspend fun authorizePayment(authorizePaymentRequest: AuthorizePaymentRequest): Single<AuthorizePaymentResponse> = coroutineScope {
        val remoteJob = async(dispatchers.io) { remotePaymentMethodDataSource.authorizePayment(authorizePaymentRequest) }
        return@coroutineScope when (val result = remoteJob.await()) {
            is Success -> Single.just(result.data)
            is ErrorResult -> Single.error(result.exception)//TODO: Biju: Show Toast/SnackBar
        }
    }
}