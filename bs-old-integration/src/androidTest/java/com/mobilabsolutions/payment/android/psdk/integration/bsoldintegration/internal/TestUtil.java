package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.internal;

import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentSdk;

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
public class TestUtil {

    public static void resetPaymentSdk() {
        NewPaymentSdk.Companion.reset();

    }
}
