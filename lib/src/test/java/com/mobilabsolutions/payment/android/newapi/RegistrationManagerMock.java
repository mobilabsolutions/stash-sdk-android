package com.mobilabsolutions.payment.android.newapi;

import com.mobilabsolutions.payment.android.psdk.RegistrationManager;
import com.mobilabsolutions.payment.android.psdk.model.BillingData;
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData;
import com.mobilabsolutions.payment.android.psdk.model.SepaData;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
public class RegistrationManagerMock implements RegistrationManager{
    @Override
    public Single<String> registerCreditCard(CreditCardData creditCardData) {
        return Single.just("1234-CC");
    }

    @Override
    public Single<String> registerSepa(SepaData sepaData, BillingData billingData) {
        return Single.just("1234-SEPA");
    }

}
