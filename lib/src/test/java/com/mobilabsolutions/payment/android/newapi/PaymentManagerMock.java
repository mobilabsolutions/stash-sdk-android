package com.mobilabsolutions.payment.android.newapi;

import com.mobilabsolutions.payment.android.psdk.PaymentManager;
import com.mobilabsolutions.payment.android.psdk.model.BillingData;
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData;
import com.mobilabsolutions.payment.android.psdk.model.PaymentData;

import io.reactivex.Single;

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
public class PaymentManagerMock implements PaymentManager {
    @Override
    public Single<String> executeCreditCardPayment(CreditCardData creditCardData, BillingData billingData, PaymentData paymentData) {
        return Single.just("123");
    }

    @Override
    public Single<String> executeCreditCardPaymentWithAlias(String creditCardAlias, PaymentData paymentData) {
        return Single.just("123");
    }

    @Override
    public Single<String> executeSepaPaymentWithAlias(String sepaAlias, PaymentData paymentData) {
        return Single.just("123");
    }

    @Override
    public Single<String> executePayPalPayment(PaymentData paymentData, BillingData billingData) {
        return Single.just("123");
    }
}
