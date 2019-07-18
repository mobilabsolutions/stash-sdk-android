/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import android.app.Application
import android.graphics.Point
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import com.mobilabsolutions.payment.android.BuildConfig
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreeIntegration
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreePayPalActivity
import com.mobilabsolutions.payment.android.psdk.integration.braintree.R
import com.mobilabsolutions.payment.android.psdk.internal.SslSupportModule
import com.mobilabsolutions.payment.android.psdk.internal.StashComponent
import com.mobilabsolutions.payment.android.psdk.internal.StashModule
import dagger.Component
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PayPalRegistrationTest {
    val MOBILAB_BACKEND_URL = BuildConfig.mobilabBackendUrl
    val MOBILAB_TEST_PUBLISHABLE_KEY = BuildConfig.newBsTestKey

    @get:Rule
    val activityRule = object : ActivityTestRule<BraintreePayPalActivity>(BraintreePayPalActivity::class.java, true, false) {

        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            val context = InstrumentationRegistry.getInstrumentation().context
            val methods = setOf(PaymentMethodType.PAYPAL)
            val initialization = BraintreeIntegration.create(methods)
            val component = DaggerTestPayPalRegistrationComponent.builder()
                .stashModule(StashModule(
                    MOBILAB_TEST_PUBLISHABLE_KEY,
                    MOBILAB_BACKEND_URL,
                    context.applicationContext as Application,
                    mapOf(initialization to methods), true))
                .build()
            initialization.initialize(component)
        }
    }

    @Before
    fun setUp() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val coordinates = Array<Point>(4) { Point(0, 0) }
        coordinates[0] = Point(248, 1520)
        coordinates[1] = Point(248, 929)
        coordinates[2] = Point(796, 1520)
        coordinates[3] = Point(796, 929)

        if (!uiDevice.isScreenOn) {
            uiDevice.wakeUp()
            uiDevice.swipe(coordinates, 10)
        }
    }

    @Ignore("Failing on travis, seems that emulator screen goes to sleep")
    @Test
    fun checkLoading() {
        activityRule.launchActivity(null)
        onView(withId(R.id.paypal_progress)).check(matches(isDisplayed()))
    }
}

@Singleton
@Component(modules = [SslSupportModule::class, StashModule::class])
internal interface TestPayPalRegistrationComponent : StashComponent {
    fun injectTest(test: PayPalRegistrationTest)
}