package com.mobilabsolutions.payment.sample.data.repositories.user

import com.mobilabsolutions.payment.sample.data.RetrofitRunner
import com.mobilabsolutions.payment.sample.data.entities.Result
import com.mobilabsolutions.payment.sample.data.entities.User
import com.mobilabsolutions.payment.sample.data.mappers.CreateUserResponseToUser
import com.mobilabsolutions.payment.sample.network.SampleMerchantService
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
class RemoteUserDataSource @Inject constructor(
    private val retrofitRunner: RetrofitRunner,
    private val sampleMerchantService: SampleMerchantService,
    private val createUserResponseToUser: CreateUserResponseToUser
) {

    suspend fun createUser(): Result<User> {
        return retrofitRunner.executeForResponse(createUserResponseToUser) {
            sampleMerchantService.createUser().execute()
        }
    }
}