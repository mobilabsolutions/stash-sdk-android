/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.sample.data.repositories.user

import com.mobilabsolutions.payment.sample.data.DatabaseTransactionRunner
import com.mobilabsolutions.payment.sample.data.daos.EntityInserter
import com.mobilabsolutions.payment.sample.data.daos.UserDao
import com.mobilabsolutions.payment.sample.data.entities.User
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
class LocalUserStore @Inject constructor(
    private val transactionRunner: DatabaseTransactionRunner,
    private val entityInserter: EntityInserter,
    private val userDao: UserDao
) {
    suspend fun getUserCount() = userDao.userCount()

    fun observerUser() = userDao.entryObservable()

    suspend fun createUser(): User = transactionRunner {
        userDao.insert(User(userId = "empty_user_id"))
        userDao.getUser()
    }

    suspend fun saveUser(user: User) = transactionRunner {
        entityInserter.insertOrUpdate(userDao, user)
    }
}