package com.mobilabsolutions.payment.sample;

import com.mobilabsolutions.payment.android.psdk.model.BillingData;
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData;

import org.threeten.bp.LocalDate;

/**
 *
 * Find more <a href="https://github.com/bumptech/glide">Glide</a>
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 *
 */
public class JavaSketch {

    public void bla() {
        BillingData billingData = BillingData.fromEmail("bla@bla.com");
        BillingData builtBillingData = new BillingData.Builder().build();

        CreditCardData creditCardData = new CreditCardData(
                "123",
                LocalDate.of(2011,11,1),
                "123",
                "Bla"
        );

//        StripeAdditionalRegistrationData stripeAdditionalRegistrationData = new StripeAdditionalRegistrationData();


//        StripeIntegration stripeIntegration = new StripeIntegration();
//        stripeIntegration.executePayoneRequest(null, creditCardData, stripeAdditionalRegistrationData);


//        PaymentSdk.getRegistrationManager().executePayoneRequest(creditCardData, billingData);
//        ProviderOriginatedException exception = new TemporaryException();
//        switch (exception.getCode()) {
//            case TemporaryException.CODE:
//                Timber.d("Temporary error");
//                default:
//                    Timber.d("Other error");
//        }
    }
}
