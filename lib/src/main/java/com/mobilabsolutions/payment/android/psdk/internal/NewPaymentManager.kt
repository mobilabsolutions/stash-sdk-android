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


    override fun executeCreditCardPayment(
            creditCardData: CreditCardData,
            paymentData: PaymentData): Single<String> {
        return pspCoordinator.handleExecuteCreditCardPayment(creditCardData, paymentData)
    }

    override fun executeCreditCardPaymentWithAlias(
            creditCardAlias: String,
            paymentData: PaymentData): Single<String> {
        return pspCoordinator.handleExecuteCreditCardPaymentWithAlias(creditCardAlias, paymentData)
    }

    override fun executeSepaPaymentWithAlias(
            sepaAlias: String, paymentData: PaymentData): Single<String> {
        return pspCoordinator.handleExecuteSepaPaymentWithAlias(sepaAlias, paymentData)
    }

    override fun executePayPalPayment(paymentData: PaymentData, billingData: BillingData): Single<String> {
        return pspCoordinator.handleExecutePaypalPayment(paymentData, billingData)
    }

}