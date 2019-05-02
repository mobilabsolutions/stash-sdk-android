package com.mobilabsolutions.payment.sample;

import android.app.Application;

import com.mobilabsolutions.payment.android.psdk.PaymentSdk;
import com.mobilabsolutions.payment.android.psdk.PaymentSdkConfiguration;
import com.mobilabsolutions.payment.android.psdk.RegistrationManager;
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreeIntegration;
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration;
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion;
import com.mobilabsolutions.payment.android.psdk.model.BillingData;
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData;

import org.threeten.bp.LocalDate;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import io.reactivex.disposables.Disposable;

/**
 * Find more <a href="https://github.com/bumptech/glide">Glide</a>
 *
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
public class JavaSketch {

    public void bla() {
        BillingData billingData = BillingData.fromEmail("bla@bla.com");
        BillingData builtBillingData = new BillingData.Builder().build();
        Application context = null;

        PaymentSdk.initalize(BuildConfig.newBsApiKey, BuildConfig.mobilabBackendUrl, context, BsPayoneIntegration.Companion);
        RegistrationManager registrationManager = PaymentSdk.getRegistrationManager();

        CreditCardData creditCardData = CreditCardData.create(
                "123",
                LocalDate.of(2011, 11, 1),
                "123",
                "Bla"
        );

        Disposable disposable = registrationManager.registerCreditCard(creditCardData, BillingData.empty(), UUID.randomUUID())
                .subscribe(
                        alias -> {
                            //Handle alias
                        },
                        error -> {
                            //Handle error
                        }
                );
        Set<IntegrationCompanion> integrations = new HashSet<>();
        integrations.add(BraintreeIntegration.Companion);
        integrations.add(BsPayoneIntegration.Companion);

        PaymentSdkConfiguration configuration = new PaymentSdkConfiguration.Builder("YourPublicKey")
                .setEndpoint("https://payment-dev.mblb.net/api/")
                .setIntegrations(integrations)
                .setTestMode(true)
                .build();

        PaymentSdk.initalize(context, configuration);

        registrationManager.registerPaymentMehodUsingUi(null, null, null);

//        UiDetailType uiDetailType = UiDetailType.NAME

//        StripeAdditionalRegistrationData stripeAdditionalRegistrationData = new StripeAdditionalRegistrationData();


//        StripeIntegration stripeIntegration = new StripeIntegration();
//        stripeIntegration.executePayoneRequest(null, creditCardData, stripeAdditionalRegistrationData);


//        PaymentSdk.getRegistrationManager().executePayoneRequest(creditCardData, billingData);
//        BasePaymentException exception = new TemporaryException();
//        switch (exception.getCode()) {
//            case TemporaryException.CODE:
//                Timber.d("Temporary error");
//                default:
//                    Timber.d("Other error");
//        }
    }
}
