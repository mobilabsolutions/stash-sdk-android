package com.mobilabsolutions.payment.android.psdk.internal

import com.mobilabsolutions.payment.android.psdk.PaymentManager
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.PaymentData
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class NewPaymentManager @Inject constructor(
        private val pspCoordinator: PspCoordinator
) : PaymentManager {

    //TODO we are keeping this class here until we are clear on how paypal will be handled
    //and how are we going to trigger klarna and similar customer-authenticated payments

    override fun executePayPalPayment(paymentData: PaymentData, billingData: BillingData): Single<String> {
        return pspCoordinator.handleExecutePaypalPayment(paymentData, billingData)
    }

}