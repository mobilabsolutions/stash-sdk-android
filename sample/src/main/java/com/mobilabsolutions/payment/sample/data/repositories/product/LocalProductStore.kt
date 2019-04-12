package com.mobilabsolutions.payment.sample.data.repositories.product

import com.mobilabsolutions.payment.sample.data.DatabaseTransactionRunner
import com.mobilabsolutions.payment.sample.data.daos.EntityInserter
import com.mobilabsolutions.payment.sample.data.daos.ProductDao
import com.mobilabsolutions.payment.sample.data.entities.Product
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
class LocalProductStore @Inject constructor(
        private val entityInserter: EntityInserter,
        private val transactionRunner: DatabaseTransactionRunner,
        private val productDao: ProductDao
) {
    companion object {
        private val mobilabTShirt = Product(
                name = "Mobilab",
                description = "T-Shirt print",
                price = 2385
        )

        private val notebookPaper = Product(
                name = "Notebook Paper",
                description = "Quadrille Pads",
                price = 350
        )

        private val sticker = Product(
                name = "MobiLab Sticker",
                description = "12 sticker sheets",
                price = 2395
        )

        private val pen = Product(
                name = "MobiLab Pen",
                description = "Blue Color",
                price = 2395
        )

        private val femaleTShirt = Product(
                name = "MobiLab",
                description = "Female",
                price = 2395
        )

        private val sampleDataList = listOf(mobilabTShirt, notebookPaper, sticker, pen, femaleTShirt)
        private const val ITEM_SIZE = 5
    }

    suspend fun isInitData(): Boolean = productDao.productCount() < ITEM_SIZE

    suspend fun populateInitData() = transactionRunner {
        productDao.insertAll(sampleDataList)
    }

    fun observerProducts() = productDao.entriesObservable()
}