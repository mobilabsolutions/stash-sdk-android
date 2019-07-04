/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.sample.data.repositories.user

import com.mobilabsolutions.payment.sample.data.entities.User
import io.reactivex.Observable

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
interface UserRepository {
    fun observerUser(): Observable<User>
    suspend fun updateUser()
}