package com.mobilabsolutions.payment.sample.data.interactors

import com.mobilabsolutions.payment.sample.core.ChannelInteractor
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.data.repositories.paymentmethod.PaymentMethodRepository
import com.mobilabsolutions.payment.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 25-04-2019.
 */
class AddPaymentMethod @Inject constructor(
    dispatchers: AppCoroutineDispatchers,
    private val paymentMethodRepository: PaymentMethodRepository
) : ChannelInteractor<AddPaymentMethod.ExecuteParams, Unit>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override suspend fun execute(executeParams: ExecuteParams) {
        paymentMethodRepository.addPaymentMethod(userId = executeParams.userId, aliasId = executeParams.aliasId, paymentMethod = executeParams.paymentMethod)
    }

    data class ExecuteParams(val userId: String, val aliasId: String, val paymentMethod: PaymentMethod)
}