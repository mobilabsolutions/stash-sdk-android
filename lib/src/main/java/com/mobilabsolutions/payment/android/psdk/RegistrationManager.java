package com.mobilabsolutions.payment.android.psdk;

import com.mobilabsolutions.payment.android.psdk.model.BillingData;
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData;
import com.mobilabsolutions.payment.android.psdk.model.SepaData;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
public interface RegistrationManager {

    /**
     * Register a credit card so you can use payment alias for future payments
     * @param creditCardData credit card information
     * @return string representing payment alias
     */
    Single<String> registerCreditCard(CreditCardData creditCardData);

    /**
     * Register a sepa debit account so you can use payment alias for future payments
     * @param sepaData sepa card information
     * @return string representing payment alias
     */
    Single<String> registerSepa(SepaData sepaData);

    /**
     * Remove credit card alias, deregistering it from your payment service provider
     * @param alias payment alias to deregister
     * @return Observable signifying success or failure
     */
    Completable removeCreditCardAlias(String alias);

    /**
     * Remove Sepa alias, deregistering it from your payment service provider
     * @param alias payment alias to deregister
     * @return Observable signifying success or failure
     */
    Completable removeSepaAlias(String alias);
}
