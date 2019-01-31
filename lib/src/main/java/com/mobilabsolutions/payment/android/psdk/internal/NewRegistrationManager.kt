package com.mobilabsolutions.payment.android.psdk.internal

import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class NewRegistrationManager @Inject constructor(
        private val pspCoordinator: PspCoordinator

) : RegistrationManager {
    override fun registerCreditCard(creditCardData: CreditCardData): Single<String> {
        return pspCoordinator.handleRegisterCreditCard(creditCardData)
    }


    override fun registerSepa(sepaData: SepaData): Single<String> {
        return pspCoordinator.handleRegisterSepa(sepaData)
    }

    override fun removeCreditCardAlias(alias: String): Completable {
        return pspCoordinator.handleRemoveCreditCardAlias(alias)
    }

    override fun removeSepaAlias(alias: String): Completable {
        return pspCoordinator.handleRemoveSepaAlias(alias)
    }

}