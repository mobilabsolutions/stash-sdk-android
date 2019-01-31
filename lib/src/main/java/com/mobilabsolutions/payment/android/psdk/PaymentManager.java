package com.mobilabsolutions.payment.android.psdk;

import com.mobilabsolutions.payment.android.psdk.model.BillingData;
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData;
import com.mobilabsolutions.payment.android.psdk.model.PaymentData;

import io.reactivex.Single;

/**
 * Payment manager enables you to execute payments either by providing credit card data,
 * or by providing a payment alias you have already configure using {@link RegistrationManager}
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
public interface PaymentManager {

    /**
     * Execute payment by providing {@link CreditCardData credit card data}, {@link BillingData billing data}
     * and {@link PaymentData payment data}. If transaction is successful you will receive an id of
     * the transaction that can be used later to reverse or refund the transaction
     * @param creditCardData credit card information
     * @param paymentData data that defines current transaction
     * @return String representing the transaction id
     */
    Single<String> executeCreditCardPayment(CreditCardData creditCardData,
                                            PaymentData paymentData
    );

    /**
     * Execute a payment by providing credit card payment alias registered with the {@link RegistrationManager}
     * and {@link PaymentData payment data} required for the current transaction
     * @param creditCardAlias Alias of the credit card
     * @param paymentData data that defines current transaction
     * @return String representing the transaction id
     */
    Single<String> executeCreditCardPaymentWithAlias(
            String creditCardAlias,
            PaymentData paymentData
    );

    /**
     * Execute a payment by providing sepa payment alias registered with the {@link RegistrationManager}
     * and {@link PaymentData payment data} required for the current transaction
     * @param sepaAlias Alias of the sepa account
     * @param paymentData data that defines current transaction
     * @return String representing the transaction id
     */
    Single<String> executeSepaPaymentWithAlias(
            String sepaAlias,
            PaymentData paymentData
    );

    /**
     * Execute a payment using paypal, the customer will be redirected to paypal login page, where
     * they can confirm the payment, {@link PaymentData payment data} contains the data required for
     * the current transaction
     * @param paymentData data that defines current transaction
     * @param billingData customers billing data
     * @return transaction id
     */
    Single<String> executePayPalPayment(
            PaymentData paymentData,
            BillingData billingData
    );
}
