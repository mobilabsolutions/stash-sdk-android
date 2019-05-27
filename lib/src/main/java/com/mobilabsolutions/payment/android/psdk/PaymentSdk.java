package com.mobilabsolutions.payment.android.psdk;

import android.app.Application;

import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentSdk;

/**
 * This is a main interface to Payment SDK.
 * <p>
 * It is used by providing a public key to the initialize method, and retrieving {@link RegistrationManager}
 * and {@link UiCustomizationManager} instances to setup your payment methods and customize UI components, respectively
 *
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
public final class PaymentSdk {



    public static void initalize(Application applicationContext, PaymentSdkConfiguration paymentSdkConfiguration) {
        NewPaymentSdk.Companion.initialize(applicationContext, paymentSdkConfiguration);
    }

    public static void configureUi(PaymentUIConfiguration paymentUIConfiguration) {
        NewPaymentSdk.Companion.configureUi(paymentUIConfiguration);
    }


    /**
     * Retrieve the instance of registration manager used to register various payment methods
     *
     * @return registration manager
     */
    public static RegistrationManager getRegistrationManager() {
        return NewPaymentSdk.Companion.getRegistrationManager();
    }


    public static UiCustomizationManager getUiCustomizationManager() {
        return NewPaymentSdk.Companion.getUiCustomizationManager();
    }

}
