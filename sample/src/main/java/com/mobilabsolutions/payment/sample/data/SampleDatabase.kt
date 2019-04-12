package com.mobilabsolutions.payment.sample.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mobilabsolutions.payment.sample.data.daos.CartDao
import com.mobilabsolutions.payment.sample.data.daos.ProductDao
import com.mobilabsolutions.payment.sample.data.entities.Cart
import com.mobilabsolutions.payment.sample.data.entities.Product

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Database(
        entities = [
            Product::class,
            Cart::class
        ],
        version = 1
)
abstract class SampleDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
}
