package com.mobilabsolutions.payment.android.psdk;

import android.app.Application;

import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentSdk;

/**
 * This is a main interface to Payment SDK.
 * <p>
 * It is used by providing a configuration object to the initialize method, and retrieving {@link RegistrationManager}
 * instance to register your payment methods. You can also customize UI components by providing customization configuration.
 *
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
public final class PaymentSdk {


    /**
     * Initialize the SDK by using configuration object {@link PaymentSdkConfiguration}. This needs to be done before the Payment SDK is used
     * for the first time.
     * @param applicationContext Application context
     * @param paymentSdkConfiguration SDK configuration object
     */
    public static void initalize(Application applicationContext, PaymentSdkConfiguration paymentSdkConfiguration) {
        NewPaymentSdk.Companion.initialize(applicationContext, paymentSdkConfiguration);
    }

    /**
     * Provide a customization object for UI components so it blends better with the rest of your application.
     * @param paymentUIConfiguration ui configuration object
     */
    public static void configureUi(PaymentUiConfiguration paymentUIConfiguration) {
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

}
