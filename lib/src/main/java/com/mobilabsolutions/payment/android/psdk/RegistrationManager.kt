package com.mobilabsolutions.payment.android.psdk

import android.app.Activity
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

    /**
     * Returns a list of supported payment methods
     * @return list of supported payment methods
     */
    fun getAvailablePaymentMethods() : Set<PaymentMethodType>

    /**
     * Let payment SDK handle data using built-in UI components
     *
     * @param activity the activity context to launch from. If activity is null, a new task will be created
     * @param specificPaymentMethodType skip payment method chooser and immediately show specific type entry UI
     * @returnstring string representing aliasId
     */
    fun registerPaymentMehodUsingUi(activity : Activity? = null, specificPaymentMethodType: PaymentMethodType? = null) : Single<String>


}
