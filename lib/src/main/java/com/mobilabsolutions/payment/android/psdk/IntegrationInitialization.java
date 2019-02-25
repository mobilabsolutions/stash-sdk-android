package com.mobilabsolutions.payment.android.psdk;

import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent;
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.Integration;

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
public interface IntegrationInitialization {
    Integration initialize(PaymentSdkComponent paymentSdkComponent);
}
