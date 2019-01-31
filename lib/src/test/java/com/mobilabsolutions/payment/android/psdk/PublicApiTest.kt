package com.mobilabsolutions.payment.android.psdk

import android.util.Base64
import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentSdk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(Base64::class)
@PowerMockIgnore("javax.net.ssl.*")
class PublicApiTest {

//    val paymentSdk : PaymentSdk = NewPaymentSdk("123")

    @Before
    fun setUp()
    {
        NewPaymentSdk.reset()
    }

    @Test
    fun testCardRegistration() {

    }



}