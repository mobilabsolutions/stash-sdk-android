package com.mobilabsolutions.payment.sample.data.interactors

import com.mobilabsolutions.payment.sample.core.SubjectInteractor
import com.mobilabsolutions.payment.sample.data.repositories.cart.CartRepository
import com.mobilabsolutions.payment.sample.data.resultentities.CartWithProduct
import com.mobilabsolutions.payment.sample.util.AppCoroutineDispatchers
import com.mobilabsolutions.payment.sample.util.AppRxSchedulers
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 15-04-2019.
 */
class LoadCart @Inject constructor(
        dispatchers: AppCoroutineDispatchers,
        private val schedulers: AppRxSchedulers,
        private val cartRepository: CartRepository
) : SubjectInteractor<Unit, Unit, List<CartWithProduct>>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override fun createObservable(params: Unit): Observable<List<CartWithProduct>> {
        return cartRepository.observeCarts()
                .subscribeOn(schedulers.io)
    }

    override suspend fun execute(params: Unit, executeParams: Unit) {
        // do nothing
    }
}