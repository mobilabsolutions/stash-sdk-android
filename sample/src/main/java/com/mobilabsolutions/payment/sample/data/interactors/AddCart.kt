package com.mobilabsolutions.payment.sample.data.interactors

import com.mobilabsolutions.payment.sample.core.ChannelInteractor
import com.mobilabsolutions.payment.sample.data.entities.Product
import com.mobilabsolutions.payment.sample.data.repositories.cart.CartRepository
import com.mobilabsolutions.payment.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 15-04-2019.
 */
class AddCart @Inject constructor(
    dispatchers: AppCoroutineDispatchers,
    private val cartRepository: CartRepository
) : ChannelInteractor<AddCart.ExecuteParams, Unit>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override suspend fun execute(executeParams: ExecuteParams) {
        cartRepository.addProductToCart(executeParams.product.id)
    }

    data class ExecuteParams(val product: Product)
}