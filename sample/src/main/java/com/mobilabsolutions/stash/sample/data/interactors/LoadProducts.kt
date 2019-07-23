/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.interactors

import com.mobilabsolutions.stash.sample.core.SubjectInteractor
import com.mobilabsolutions.stash.sample.data.entities.Product
import com.mobilabsolutions.stash.sample.data.repositories.product.ProductRepository
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
import com.mobilabsolutions.stash.sample.util.AppRxSchedulers
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
class LoadProducts @Inject constructor(
    dispatchers: AppCoroutineDispatchers,
    private val schedulers: AppRxSchedulers,
    private val productRepository: ProductRepository
) : SubjectInteractor<Unit, Unit, List<Product>>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override fun createObservable(params: Unit): Observable<List<Product>> {
        return productRepository.observerProducts()
                .subscribeOn(schedulers.io)
    }

    override suspend fun execute(params: Unit, executeParams: Unit) {
        // do nothing
    }
}