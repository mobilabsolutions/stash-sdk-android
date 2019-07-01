package com.mobilabsolutions.payment.android.psdk.internal;

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
public class TestUtil {

    public static void resetPaymentSdk() {
        PaymentSdkImpl.Companion.reset();

    }
}
