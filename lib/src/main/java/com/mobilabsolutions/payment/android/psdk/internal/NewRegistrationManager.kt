package com.mobilabsolutions.payment.android.psdk.internal

import android.app.Activity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodAlias
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.Single
import java.util.UUID
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class NewRegistrationManager @Inject constructor(
    private val pspCoordinator: PspCoordinator

) : RegistrationManager {

    override fun registerCreditCard(creditCardData: CreditCardData, billingData: BillingData, idempotencyKey: UUID?): Single<PaymentMethodAlias> {
        return pspCoordinator.handleRegisterCreditCard(creditCardData = creditCardData, billingData = billingData, idempotencyKey = (idempotencyKey
            ?: UUID.randomUUID()).toString())
    }

    override fun registerSepa(sepaData: SepaData, billingData: BillingData, idempotencyKey: UUID?): Single<PaymentMethodAlias> {
        return pspCoordinator.handleRegisterSepa(sepaData = sepaData, billingData = billingData, idempotencyKey = (idempotencyKey
            ?: UUID.randomUUID()).toString())
    }

    override fun getAvailablePaymentMethods(): Set<PaymentMethodType> {
        return pspCoordinator.getAvailablePaymentMethods()
    }

    override fun registerPaymentMehodUsingUi(activity: Activity?, specificPaymentMethodType: PaymentMethodType?, idempotencyKey: UUID?): Single<PaymentMethodAlias> {
        return pspCoordinator.handleRegisterPaymentMethodUsingUi(activity, specificPaymentMethodType, (idempotencyKey
            ?: UUID.randomUUID()).toString())
    }
}