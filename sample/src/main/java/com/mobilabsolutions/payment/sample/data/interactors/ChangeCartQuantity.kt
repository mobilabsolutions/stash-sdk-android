package com.mobilabsolutions.payment.sample.data.interactors

import com.mobilabsolutions.payment.sample.core.ChannelInteractor
import com.mobilabsolutions.payment.sample.data.repositories.cart.CartRepository
import com.mobilabsolutions.payment.sample.data.resultentities.CartWithProduct
import com.mobilabsolutions.payment.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 15-04-2019.
 */
class ChangeCartQuantity @Inject constructor(
        dispatchers: AppCoroutineDispatchers,
        private val cartRepository: CartRepository
) : ChannelInteractor<ChangeCartQuantity.ExecuteParams, Unit>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override suspend fun execute(executeParams: ExecuteParams) {
        cartRepository.changeCartQuantity(executeParams.add, executeParams.cartWithProduct)
    }


    data class ExecuteParams(val add: Boolean, val cartWithProduct: CartWithProduct)
}