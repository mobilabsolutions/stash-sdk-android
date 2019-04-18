package com.mobilabsolutions.payment.sample.data.repositories.product

import com.mobilabsolutions.payment.sample.data.entities.Product
import io.reactivex.Observable

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
interface ProductRepository {
    fun observerProducts(): Observable<List<Product>>
}