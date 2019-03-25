package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import android.app.Application
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.rule.ActivityTestRule
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreePayPalActivity
import com.mobilabsolutions.payment.android.psdk.integration.braintree.R
import org.junit.Rule
import org.junit.Test
import android.content.Intent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matchers.allOf
import androidx.test.espresso.intent.Intents.intending
import android.app.Instrumentation
import androidx.test.platform.app.InstrumentationRegistry
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreeIntegration
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkComponent
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkModule
import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import dagger.Component
import org.junit.Before
import java.util.concurrent.CountDownLatch
import javax.inject.Singleton


/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PayPalRegistrationTest {
    val MOBILAB_BACKEND_URL = BuildConfig.mobilabBackendUrl
    val MOBILAB_TEST_PUBLISHABLE_KEY = BuildConfig.newBsTestKey

    @get:Rule val activityRule = object : ActivityTestRule<BraintreePayPalActivity>(BraintreePayPalActivity::class.java, true, false) {


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


    @Before
    fun setUp() {


    }

    @Test
    fun checkLoading() {
        activityRule.launchActivity(null)
        onView(withId(R.id.paypal_progress)).check(matches(isDisplayed()))
    }


}

@Singleton
@Component(modules = [SslSupportModule::class, PaymentSdkModule::class])
internal interface TestPayPalRegistrationComponent : PaymentSdkComponent {
    fun injectTest(test: PayPalRegistrationTest)
}