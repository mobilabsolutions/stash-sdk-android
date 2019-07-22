/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.interactors

import com.mobilabsolutions.stash.sample.core.SubjectInteractor
import com.mobilabsolutions.stash.sample.data.entities.User
import com.mobilabsolutions.stash.sample.data.repositories.user.UserRepository
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
import com.mobilabsolutions.stash.sample.util.AppRxSchedulers
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
class UpdateUser @Inject constructor(
    dispatchers: AppCoroutineDispatchers,
    private val schedulers: AppRxSchedulers,
    private val userRepository: UserRepository
) : SubjectInteractor<Unit, Unit, User>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override fun createObservable(params: Unit): Observable<User> {
        return userRepository.observerUser()
                .subscribeOn(schedulers.io)
    }

    override suspend fun execute(params: Unit, executeParams: Unit) {
        userRepository.updateUser()
    }
}