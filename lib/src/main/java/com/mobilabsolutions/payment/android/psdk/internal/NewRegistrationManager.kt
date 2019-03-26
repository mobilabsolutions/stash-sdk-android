package com.mobilabsolutions.payment.android.psdk.internal

import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class NewRegistrationManager @Inject constructor(
        private val pspCoordinator: PspCoordinator

) : RegistrationManager {

    override fun registerCreditCard(creditCardData: CreditCardData, billingData: BillingData): Single<String> {
        return pspCoordinator.handleRegisterCreditCard(creditCardData = creditCardData, billingData = billingData ?: BillingData())
    }


    override fun registerSepa(sepaData: SepaData, billingData : BillingData): Single<String> {
        return pspCoordinator.handleRegisterSepa(sepaData = sepaData, billingData = billingData )
    }

    override fun registerCreditCardUsingUIComponent(): Single<String> {
        return pspCoordinator.handleRegisterCreditCardUsingUIComponent()
    }

    override fun registerSepaUsingUIComponent(): Single<String> {
        return pspCoordinator.handleRegisterSepaUsingUIComponent()
    }

    override fun askUserToPickAPaymentMethod(): Single<PaymentMethodType> {
        return pspCoordinator.handleAskUserToChoosePaymentMethod()
    }

    override fun registerPayPalAccount(): Single<String> {
        return pspCoordinator.handleRegisterPayPal()
    }

}