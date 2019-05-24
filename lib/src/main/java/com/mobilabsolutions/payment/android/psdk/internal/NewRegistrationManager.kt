package com.mobilabsolutions.payment.android.psdk.internal

import android.app.Activity
import com.mobilabsolutions.payment.android.psdk.PaymentMethodAlias
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
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

    override fun registerCreditCard(creditCardData: CreditCardData, idempotencyKey: UUID?): Single<PaymentMethodAlias> {
        return pspCoordinator.handleRegisterCreditCard(creditCardData = creditCardData, idempotencyKey = (idempotencyKey
            ?: UUID.randomUUID()).toString())
    }

    override fun registerSepaAccount(sepaData: SepaData, idempotencyKey: UUID?): Single<PaymentMethodAlias> {
        return pspCoordinator.handleRegisterSepa(sepaData = sepaData, idempotencyKey = (idempotencyKey
            ?: UUID.randomUUID()).toString())
    }

    override fun getAvailablePaymentMethodsTypes(): Set<PaymentMethodType> {
        return pspCoordinator.getAvailablePaymentMethods()
    }

    override fun registerPaymentMethodUsingUi(activity: Activity?, specificPaymentMethodType: PaymentMethodType?, idempotencyKey: UUID?): Single<PaymentMethodAlias> {
        return pspCoordinator.handleRegisterPaymentMethodUsingUi(activity, specificPaymentMethodType, (idempotencyKey
            ?: UUID.randomUUID()).toString())
    }
}