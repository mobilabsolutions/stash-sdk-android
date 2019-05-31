package com.mobilabsolutions.payment.sample.data.mappers

import com.mobilabsolutions.payment.sample.data.entities.User
import com.mobilabsolutions.payment.sample.network.response.CreateUserResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
@Singleton
class CreateUserResponseToUser @Inject constructor() : Mapper<CreateUserResponse, User> {
    override fun map(from: CreateUserResponse): User {
        return User(userId = from.userId)
    }
}