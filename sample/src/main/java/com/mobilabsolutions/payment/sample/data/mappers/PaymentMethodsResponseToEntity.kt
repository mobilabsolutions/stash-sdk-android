package com.mobilabsolutions.payment.sample.data.mappers

import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.remote.response.PaymentMethodResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-04-2019.
 */
@Singleton
class PaymentMethodsResponseToEntity @Inject constructor() : Mapper<PaymentMethodResponse, List<PaymentMethod>> {
    override suspend fun map(from: PaymentMethodResponse): List<PaymentMethod> {
        return from.paymentMethods.map {
            PaymentMethod(
                    alias = it.alias,
                    _type = it.type
            )
        }
    }
}