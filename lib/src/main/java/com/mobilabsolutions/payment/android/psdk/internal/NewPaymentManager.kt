package com.mobilabsolutions.payment.android.psdk.internal

import com.mobilabsolutions.payment.android.psdk.PaymentManager
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class NewPaymentManager @Inject constructor(
    private val pspCoordinator: PspCoordinator
) : PaymentManager