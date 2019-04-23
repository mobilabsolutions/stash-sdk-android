package com.mobilabsolutions.payment.sample.data.repositories.paymentmethod

import com.mobilabsolutions.payment.sample.data.RetrofitRunner
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.data.entities.Result
import com.mobilabsolutions.payment.sample.data.mappers.PaymentMethodsResponseToEntity
import com.mobilabsolutions.payment.sample.remote.MerchantApi
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-04-2019.
 */
class RemotePaymentMethodsDataSource @Inject constructor(
    private val retrofitRunner: RetrofitRunner,
    private val merchantApi: MerchantApi,
    private val paymentMethodsResponseToEntity: PaymentMethodsResponseToEntity
) : PaymentMethodDataSource {
    override suspend fun getPaymentMethods(): Result<List<PaymentMethod>> {
        return retrofitRunner.executeForResponse(paymentMethodsResponseToEntity) {
            merchantApi.getPaymentMethods().execute()
        }
    }
}