package com.mobilabsolutions.payment.sample.data.repositories.product

import com.mobilabsolutions.payment.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val localProductStore: LocalProductStore
) : ProductRepository {
    init {
        GlobalScope.launch(dispatchers.io) {
            if (localProductStore.isInitData()) {
                localProductStore.populateInitData()
            }
        }
    }

    override fun observerProducts() = localProductStore.observerProducts()
}