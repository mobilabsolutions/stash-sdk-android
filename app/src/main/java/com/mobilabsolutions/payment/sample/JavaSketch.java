package com.mobilabsolutions.payment.sample;

import com.mobilabsolutions.payment.android.psdk.PaymentSdk;
import com.mobilabsolutions.payment.android.psdk.exceptions.ProviderOriginatedException;
import com.mobilabsolutions.payment.android.psdk.exceptions.other.TemporaryException;
import com.mobilabsolutions.payment.android.psdk.integration.stripeintegration.StripeAdditionalRegistrationData;
import com.mobilabsolutions.payment.android.psdk.integration.stripeintegration.StripeIntegration;
import com.mobilabsolutions.payment.android.psdk.model.BillingData;
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData;

import org.threeten.bp.LocalDate;

import timber.log.Timber;

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

        StripeAdditionalRegistrationData stripeAdditionalRegistrationData = new StripeAdditionalRegistrationData();


//        StripeIntegration stripeIntegration = new StripeIntegration();
//        stripeIntegration.registerCreditCard(null, creditCardData, stripeAdditionalRegistrationData);


//        PaymentSdk.getRegistrationManager().registerCreditCard(creditCardData, billingData);
//        ProviderOriginatedException exception = new TemporaryException();
//        switch (exception.getCode()) {
//            case TemporaryException.CODE:
//                Timber.d("Temporary error");
//                default:
//                    Timber.d("Other error");
//        }
    }
}
