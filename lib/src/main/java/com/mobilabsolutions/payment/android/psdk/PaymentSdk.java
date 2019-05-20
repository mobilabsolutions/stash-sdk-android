package com.mobilabsolutions.payment.android.psdk;

import android.app.Application;

import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization;
import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentSdk;
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;

/**
 * This is a main interface to Payment SDK.
 * <p>
 * It is used by providing a public key to the initialize method, and retrieving {@link RegistrationManager}
 * and {@link UiCustomizationManager} instances to setup your payment methods and customize UI components, respectively
 *
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
public final class PaymentSdk {

    /**
     * Used to initializa the SDK by providing public key
     *
     * @param publicKey
     * @param applicationContext context of the application
     * @param integration        PSP integration
     */


    public static void initalize(Application applicationContext, PaymentSdkConfiguration paymentSdkConfiguration) {
        NewPaymentSdk.Companion.initialize(applicationContext, paymentSdkConfiguration);
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
