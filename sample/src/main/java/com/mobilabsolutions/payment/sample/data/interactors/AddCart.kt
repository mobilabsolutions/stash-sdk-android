package com.mobilabsolutions.payment.sample.data.interactors

import com.mobilabsolutions.payment.sample.core.ChannelInteractor
import com.mobilabsolutions.payment.sample.data.daos.CartDao
import com.mobilabsolutions.payment.sample.data.daos.EntityInserter
import com.mobilabsolutions.payment.sample.data.entities.Cart
import com.mobilabsolutions.payment.sample.data.entities.Product
import com.mobilabsolutions.payment.sample.data.resultentities.CartWithProduct
import com.mobilabsolutions.payment.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 15-04-2019.
 */
class AddCart @Inject constructor(
        dispatchers: AppCoroutineDispatchers,
        private val cartDao: CartDao,
        private val entityInserter: EntityInserter
) : ChannelInteractor<AddCart.ExecuteParams, Unit>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override suspend fun execute(executeParams: ExecuteParams) {
        cartDao.cartByProductId(executeParams.product.id)?.let {
            // TODO : Do we need to notify user if it's already added?
        } ?: run {
            // Add the product with a temporary id and one quantity
            entityInserter.insertOrUpdate(cartDao, Cart(0, executeParams.product.id, 1))
        }
    }

    data class ExecuteParams(val product: Product)
}