/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.interactors

import com.mobilabsolutions.stash.sample.core.SubjectInteractor
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.data.repositories.paymentmethod.PaymentMethodRepository
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
import com.mobilabsolutions.stash.sample.util.AppRxSchedulers
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class UpdatePaymentMethods @Inject constructor(
    dispatchers: AppCoroutineDispatchers,
    private val schedulers: AppRxSchedulers,
    private val paymentMethodRepository: PaymentMethodRepository
) : SubjectInteractor<Unit, UpdatePaymentMethods.ExecuteParams, List<PaymentMethod>>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override fun createObservable(params: Unit): Observable<List<PaymentMethod>> {
        return paymentMethodRepository.observePaymentMethods()
                .subscribeOn(schedulers.io)
    }

    override suspend fun execute(params: Unit, executeParams: ExecuteParams) {
        paymentMethodRepository.updatePaymentMethods(userId = executeParams.userId)
    }

    data class ExecuteParams(val userId: String)
}