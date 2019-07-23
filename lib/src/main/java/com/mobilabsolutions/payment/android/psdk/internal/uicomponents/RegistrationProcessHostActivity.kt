/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.R
import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkImpl
import javax.inject.Inject
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import android.content.Context
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.registration_host_activity.*

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class RegistrationProcessHostActivity : AppCompatActivity() {

    @Inject
    lateinit var uiRequestHandler: UiRequestHandler

    enum class CurrentState {
        CHOOSER, ENTRY
    }

    private var currentState = CurrentState.CHOOSER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val injector = PaymentSdkImpl.getInjector()
        injector.inject(this)
        uiRequestHandler.provideHostActivity(this)
        setContentView(R.layout.registration_host_activity)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onBackPressed() {
        when (currentState) {
            CurrentState.CHOOSER -> {
                uiRequestHandler.chooserCancelled()
            }
            CurrentState.ENTRY -> {
                uiRequestHandler.entryCancelled()
            }
        }
        super.onBackPressed()
    }

    fun setState(state: CurrentState) {
        currentState = state
    }

    fun showPaypalLoading() {
        paypal_loading.visibility = VISIBLE
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}