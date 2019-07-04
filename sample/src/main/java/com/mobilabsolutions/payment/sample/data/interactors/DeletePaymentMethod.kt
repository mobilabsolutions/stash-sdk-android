/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.sample.data.interactors

import com.mobilabsolutions.payment.sample.core.ChannelInteractor
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.data.repositories.paymentmethod.PaymentMethodRepository
import com.mobilabsolutions.payment.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class DeletePaymentMethod @Inject constructor(
    dispatchers: AppCoroutineDispatchers,
    private val paymentMethodRepository: PaymentMethodRepository
) : ChannelInteractor<DeletePaymentMethod.ExecuteParams, Unit>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override suspend fun execute(executeParams: ExecuteParams) {
        paymentMethodRepository.deletePaymentMethod(executeParams.paymentMethod)
    }

    data class ExecuteParams(val paymentMethod: PaymentMethod)
}