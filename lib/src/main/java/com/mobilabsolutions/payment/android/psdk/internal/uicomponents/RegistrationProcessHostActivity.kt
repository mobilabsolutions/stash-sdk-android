/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.R
import com.mobilabsolutions.payment.android.psdk.internal.StashImpl
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import javax.inject.Inject

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
        val injector = StashImpl.getInjector()
        injector.inject(this)
        uiRequestHandler.provideHostActivity(this)
        setContentView(R.layout.registration_host_activity)
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

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}