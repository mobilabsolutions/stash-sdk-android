package com.mobilabsolutions.payment.android.psdk.integration.bspayone

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreePayPalActivity
import com.mobilabsolutions.payment.android.psdk.integration.braintree.R
import org.junit.Rule
import org.junit.Test

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PayPalRegistrationTest {

    @get:Rule val activityRule = ActivityTestRule(BraintreePayPalActivity::class.java)

    @Test
    fun checkLoading() {
        onView(withId(R.id.paypal_progress)).check(matches(isDisplayed()))
    }
}