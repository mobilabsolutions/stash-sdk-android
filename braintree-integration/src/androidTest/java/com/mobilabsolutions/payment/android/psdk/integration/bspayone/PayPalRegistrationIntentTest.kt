package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import android.app.Activity
import android.app.Application
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.rule.ActivityTestRule
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreePayPalActivity
import com.mobilabsolutions.payment.android.psdk.integration.braintree.R
import org.junit.Rule
import org.junit.Test
import android.content.Intent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matchers.allOf
import androidx.test.espresso.intent.Intents.intending
import android.app.Instrumentation
import android.graphics.Point
import android.os.RemoteException
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreeIntegration
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkModule
import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import dagger.Component
import lib.android.paypal.com.magnessdk.a.b.e
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Ignore
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PayPalIntentRegistrationTest {
    val MOBILAB_BACKEND_URL = BuildConfig.mobilabBackendUrl
    val MOBILAB_TEST_PUBLISHABLE_KEY = BuildConfig.newBsTestKey


    @get:Rule
    val intentsTestRule = object : IntentsTestRule<BraintreePayPalActivity>(BraintreePayPalActivity::class.java, false, false) {
        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            val context = InstrumentationRegistry.getInstrumentation().context
            val initialization = BraintreeIntegration.create()
            val component = DaggerTestPayPalRegistrationComponent.builder()
                    .paymentSdkModule(PaymentSdkModule(
                            MOBILAB_TEST_PUBLISHABLE_KEY,
                            MOBILAB_BACKEND_URL,
                            context.applicationContext as Application ,
                            listOf(initialization)))
                    .build()
            initialization.initialize(component)
        }
    }

    lateinit var integration: BraintreeIntegration

    @Before
    fun setUp() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        val coordinates =  Array<Point>(4) { Point(0,0) }
        coordinates[0] = Point(248, 1520)
        coordinates[1] = Point(248, 929)
        coordinates[2] = Point(796, 1520)
        coordinates[3] = Point(796, 929)

        if (!uiDevice.isScreenOn()) {
            uiDevice.wakeUp();
            uiDevice.swipe(coordinates, 10);
        }

    }

    @Ignore("Failing on travis, seems that emulator screen goes to sleep")
    @Test
    fun checkBrowserIntent() {
        intentsTestRule.launchActivity(null)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
        intending(not(isInternal())).respondWith(result);
        onView(isRoot()).perform(waitFor(5000)) //We need to wait for Braintree SDK to fetch necessary data
        intended(allOf(hasAction(Intent.ACTION_VIEW)))
        Intents.assertNoUnverifiedIntents();


    }


}