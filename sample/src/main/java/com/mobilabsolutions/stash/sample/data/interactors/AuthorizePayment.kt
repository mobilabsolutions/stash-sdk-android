/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.interactors

import com.mobilabsolutions.stash.sample.core.ChannelInteractor
import com.mobilabsolutions.stash.sample.data.repositories.paymentmethod.PaymentMethodRepository
import com.mobilabsolutions.stash.sample.network.request.AuthorizePaymentRequest
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class AuthorizePayment @Inject constructor(
    dispatchers: AppCoroutineDispatchers,
    private val paymentMethodRepository: PaymentMethodRepository
) : ChannelInteractor<AuthorizePayment.ExecuteParams, Unit>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override suspend fun execute(executeParams: ExecuteParams) {
        paymentMethodRepository.authorizePayment(authorizePaymentRequest = executeParams.authorizePaymentRequest)
    }

    data class ExecuteParams(val authorizePaymentRequest: AuthorizePaymentRequest)
}