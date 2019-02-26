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
