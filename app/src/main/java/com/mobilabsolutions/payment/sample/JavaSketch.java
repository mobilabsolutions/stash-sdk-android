package com.mobilabsolutions.payment.sample;

import android.app.Application;

import com.mobilabsolutions.payment.android.psdk.IntegrationToPaymentMapping;
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType;
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
import java.util.LinkedList;
import java.util.List;
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
        BillingData billingData = new BillingData.Builder().setEmail("test@test.test").build();
        BillingData builtBillingData = new BillingData.Builder().build();
        Application context = null;

        Set<IntegrationCompanion> integrations = new HashSet<>();
        integrations.add(BsPayoneIntegration.Companion);

        PaymentSdkConfiguration paymentSdkConfiguration = new PaymentSdkConfiguration.Builder()
                .setPublishableKey(BuildConfig.newBsApiKey)
                .setEndpoint(BuildConfig.mobilabBackendUrl)
                .setIntegration(BsPayoneIntegration.Companion)
                .build();
        PaymentSdk.initalize(context, paymentSdkConfiguration);
        RegistrationManager registrationManager = PaymentSdk.getRegistrationManager();

        CreditCardData creditCardData = new CreditCardData.Builder()
                .setNumber("123123123123")
                .setCvv("123")
                .setBillingData(new BillingData.Builder().setFirstName("Holder").setLastName("Holderman").build())
                .setExpiryMonth(11)
                .setExpiryYear(2020)
                .build();


        Disposable disposable = registrationManager.registerCreditCard(creditCardData, BillingData.empty(), UUID.randomUUID())
                .subscribe(
                        alias -> {
                            //Handle alias
                        },
                        error -> {
                            //Handle error
                        }
                );
        List<IntegrationToPaymentMapping> integrations2 = new LinkedList<>();
        integrations2.add(new IntegrationToPaymentMapping(BraintreeIntegration.Companion, PaymentMethodType.PAYPAL));
        integrations2.add(new IntegrationToPaymentMapping(BsPayoneIntegration.Companion, PaymentMethodType.SEPA));

        PaymentSdkConfiguration configuration = new PaymentSdkConfiguration.Builder()
                .setPublishableKey("YourPublicKey")
                .setEndpoint("https://payment-dev.mblb.net/api/")
                .setIntegrations(integrations2)
                .setTestMode(true)
                .build();

        PaymentSdk.initalize(context, configuration);

        registrationManager.registerPaymentMethodUsingUi(null, null, null);

//        UiDetailType uiDetailType = UiDetailType.NAME

//        TemplateAdditionalRegistrationData templateAdditionalRegistrationData = new TemplateAdditionalRegistrationData();


//        TemplateIntegration templateIntegration = new TemplateIntegration();
//        templateIntegration.executePayoneRequest(null, creditCardData, templateAdditionalRegistrationData);


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
