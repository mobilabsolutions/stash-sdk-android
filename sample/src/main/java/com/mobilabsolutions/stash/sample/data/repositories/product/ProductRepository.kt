/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.product

import com.mobilabsolutions.stash.sample.data.entities.Product
import io.reactivex.Observable

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
interface ProductRepository {
    fun observerProducts(): Observable<List<Product>>
}