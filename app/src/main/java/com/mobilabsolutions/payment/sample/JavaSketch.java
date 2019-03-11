package com.mobilabsolutions.payment.sample;

import android.app.Application;
import android.content.Context;

import com.mobilabsolutions.payment.android.psdk.PaymentManager;
import com.mobilabsolutions.payment.android.psdk.PaymentSdk;
import com.mobilabsolutions.payment.android.psdk.RegistrationManager;
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration;
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.UiDetail;
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.UiDetailType;
import com.mobilabsolutions.payment.android.psdk.model.BillingData;
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData;

import org.threeten.bp.LocalDate;

import io.reactivex.disposables.Disposable;

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
        Application context = null;

        PaymentSdk.initalize(BuildConfig.newBsApiKey, context, BsPayoneIntegration.Companion);
        RegistrationManager registrationManager = PaymentSdk.getRegistrationManager();

        CreditCardData creditCardData = new CreditCardData(
                    "123",
                LocalDate.of(2011,11,1),
                "123",
                "Bla"
        );

        Disposable disposable = registrationManager.registerCreditCard(creditCardData, BillingData.empty())
                .subscribe(
                        alias -> {
                            //Handle alias
                        },
                        error -> {
                            //Handle error
                        }
                );

//        UiDetailType uiDetailType = UiDetailType.NAME

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
