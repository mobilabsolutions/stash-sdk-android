package com.mobilabsolutions.payment.sample.data.mappers

import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.network.response.PaymentMethodListResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
@Singleton
class PaymentMethodListResponseToEntity @Inject constructor() : Mapper<PaymentMethodListResponse, List<PaymentMethod>> {
    override fun map(from: PaymentMethodListResponse): List<PaymentMethod> {
        return from.paymentMethods.map {
            PaymentMethod(
                paymentMethodId = it.paymentMethodId,
                _type = it.type,
                // Credit Card
                mask = it.creditCardData?.mask ?: "",
                expiryMonth = it.creditCardData?.expiryMonth ?: "",
                expiryYear = it.creditCardData?.expiryYear ?: "",
                _cardType = it.creditCardData?.type ?: "UNKNOWN",
                // SEPA
                iban = it.sepaData?.iban ?: "",
                //PayPal
                email = it.payPalData?.email ?: ""
            )
        }
    }
}