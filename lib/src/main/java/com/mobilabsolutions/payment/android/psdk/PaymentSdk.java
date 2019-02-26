package com.mobilabsolutions.payment.android.psdk;

import android.app.Application;

import com.mobilabsolutions.payment.android.psdk.internal.IntegrationInitialization;
import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentSdk;

import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * This is a main interface to Payment SDK.
 *
 * It is used by providing a public key to the initialize method, and retrieving {@link RegistrationManager}
 * and {@link PaymentManager} instances to setup your payment methods and execute payments, respectively
 *
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
public final class PaymentSdk {

    /**
     * Used to initializa the SDK by providing public key
     * @param publicKey
     * @param applicationContext context of the application
     * @param integration PSP integration
     */
    public static void initalize(String publicKey, Application applicationContext, IntegrationInitialization integration) {
        LinkedList<IntegrationInitialization> integrationList = new LinkedList<>();
        integrationList.add(integration);
        NewPaymentSdk.Companion.initialize(publicKey, applicationContext, integrationList);
    }

    /**
     * Used to initializa the SDK by providing public key
     * @param publicKey
     * @param applicationContext context of the application
     * @param integrationList list of PSPs the SDK will be using
     */
    //TODO we're keeping this private for mvp, later on we might decide to have a server side psp chooser, or offer per request psp choice
    private static void initalize(String publicKey, Application applicationContext, List<IntegrationInitialization> integrationList) {
        NewPaymentSdk.Companion.initialize(publicKey, applicationContext, integrationList);
    }

    /**
     * Used to initialize the SDK by providing public key, ssl factory and trust manager. Use this
     * method when you want to provide your own ssl factory implementation i.e. on devices running Android SDK < 20
     * @param publicKey
     * @param sslSocketFactory
     * @param x509TrustManager
     * @param applicationContext context of the application
     */
    public static void initalize(String publicKey, Application applicationContext,  SSLSocketFactory sslSocketFactory, X509TrustManager x509TrustManager) {
        NewPaymentSdk.Companion.initialize(publicKey, applicationContext, null, sslSocketFactory, x509TrustManager);
    }

    /**
     * Retrieve the instance of registration manager used to register various payment methods
     * @return registration manager
     */
    public static RegistrationManager getRegistrationManager() {
        return NewPaymentSdk.Companion.getRegistrationManager();
    }

    /**
     * Retrieve the instance of payment manager, used to execute payments
     * @return payment manager
     */
    public static PaymentManager getPaymentManager() {
        return NewPaymentSdk.Companion.getPaymentManager();
    }

    public static UiCustomizationManager getUiCustomizationManager() {
        return NewPaymentSdk.Companion.getUiCustomizationManager();
    }

    public enum Provider {
        HYPERCHARGE("HC"),
        NEW_PAYONE("NP");

        private String prefix;

        Provider(String prefix){
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }

}
