package com.mobilabsolutions.payment.android.psdk

import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData

import io.reactivex.Completable
import io.reactivex.Single

/**
 * @author [Ugi](ugi@mobilabsolutions.com)
 */
interface RegistrationManager {

    /**
     * Register a credit card so you can use payment aliasId for future payments
     * @param creditCardData credit card information
     * @return string representing payment aliasId
     */
    fun registerCreditCard(creditCardData: CreditCardData, billingData: BillingData = BillingData()): Single<String>

    /**
     * Register a sepa debit account so you can use payment aliasId for future payments
     * @param sepaData sepa card information
     * @return string representing payment aliasId
     */
    fun registerSepa(sepaData: SepaData, billingData: BillingData = BillingData()): Single<String>

    fun registerCreditCardUsingUIComponent(): Single<String>

    fun registerSepaUsingUIComponent(): Single<String>

    fun askUseToPickAPaymentMethod() : Single<PaymentMethodType>

}
