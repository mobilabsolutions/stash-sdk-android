/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.sample.data.repositories.user

import com.mobilabsolutions.payment.sample.data.entities.ErrorResult
import com.mobilabsolutions.payment.sample.data.entities.Success
import com.mobilabsolutions.payment.sample.data.entities.User
import com.mobilabsolutions.payment.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val localUserStore: LocalUserStore,
    private val remoteUserDataSource: RemoteUserDataSource
) : UserRepository {

    init {
        GlobalScope.launch(dispatchers.io) {
            if (localUserStore.getUserCount() < 1) {
                createUser()
            }
        }
    }

    override fun observerUser() = localUserStore.observerUser()

    override suspend fun updateUser() = coroutineScope {
        // do nothing for now.
    }

    private suspend fun createUser() = coroutineScope {
        val localJob = async(dispatchers.io) { localUserStore.createUser() }
        val remoteJob = async(dispatchers.io) { remoteUserDataSource.createUser() }
        val localUser = localJob.await()
        when (val result = remoteJob.await()) {
            is Success -> localUserStore.saveUser(mergeUser(local = localUser, remote = result.data))
            is ErrorResult -> Timber.e(result.exception)
        }
    }

    private fun mergeUser(local: User, remote: User): User {
        return local.copy(
            userId = remote.userId
        )
    }
}