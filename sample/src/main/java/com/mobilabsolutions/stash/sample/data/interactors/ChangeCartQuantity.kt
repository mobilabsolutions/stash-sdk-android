/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.interactors

import com.mobilabsolutions.stash.sample.core.ChannelInteractor
import com.mobilabsolutions.stash.sample.data.repositories.cart.CartRepository
import com.mobilabsolutions.stash.sample.data.resultentities.CartWithProduct
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
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