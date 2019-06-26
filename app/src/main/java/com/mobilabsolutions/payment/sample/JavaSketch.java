package com.mobilabsolutions.payment.sample;

import android.annotation.SuppressLint;
import android.app.Application;

import com.mobilabsolutions.payment.android.psdk.ExtraAliasInfo;
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

    void showCreditCardMask(String mask) {

    }

    void showSepaMask(String mask) {

    }

    void showPayPalEmail(String email) {

    }

    void sendAliasToBackend(String alias) {

    }

    void handleException(Throwable exception) {

    }

    void handleUnknownException(Throwable exception) {

    }

    @SuppressLint("CheckResult")
    public void bla() {
//        BillingData billingData = new BillingData.Builder().setEmail("test@test.test").build();
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

        BillingData billingData = new BillingData.Builder()
                .setFirstName("Max")
                .setLastName("Mustermann")
                .build();

        CreditCardData creditCardData = new CreditCardData.Builder()
                .setNumber("123123123123")
                .setCvv("123")
                .setBillingData(billingData)
                .setExpiryMonth(11)
                .setExpiryYear(2020)
                .build();


        registrationManager.registerCreditCard(creditCardData, UUID.randomUUID())
                .subscribe(
                        paymentMethodAlias -> {
                            sendAliasToBackend(paymentMethodAlias.getAlias());
                            switch (paymentMethodAlias.getPaymentMethodType()) {
                                case CC:
                                    ExtraAliasInfo.CreditCardExtraInfo creditCardAliasInfo = paymentMethodAlias.getJavaExtraInfo().getCreditCardExtraInfo();
                                    showCreditCardMask(creditCardAliasInfo.getCreditCardMask());
                                    break;
                                case SEPA:
                                    ExtraAliasInfo.SepaExtraInfo sepaAliasInfo = paymentMethodAlias.getJavaExtraInfo().getSepaExtraInfo();
                                    //Handle showing SEPA payment method in UI i.e.:
                                    showSepaMask(sepaAliasInfo.getMaskedIban());
                                    break;
                                case PAYPAL:
                                    ExtraAliasInfo.PaypalExtraInfo paypalExtraInfo = paymentMethodAlias.getJavaExtraInfo().getPaypalExtraInfo();
                                    //Handle showing PayPal payment method in UI i.e.:
                                    showPayPalEmail(paypalExtraInfo.getEmail());

                            }
                        },
                        exception -> {
                            //Handle error
                            handleException(exception);
                        }
                );
        List<IntegrationToPaymentMapping> integrations2 = new LinkedList<>();
        integrations2.add(new IntegrationToPaymentMapping(BraintreeIntegration.Companion, PaymentMethodType.PAYPAL));
        integrations2.add(new IntegrationToPaymentMapping(BsPayoneIntegration.Companion, PaymentMethodType.SEPA));

        PaymentSdkConfiguration configuration = new PaymentSdkConfiguration.Builder()
                .setPublishableKey("YourPublishableKey")
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
//        BasePaymentException throwable = new TemporaryException();
//        switch (throwable.getCode()) {
//            case TemporaryException.CODE:
//                Timber.d("Temporary error");
//                default:
//                    Timber.d("Other error");
//        }
    }
}
