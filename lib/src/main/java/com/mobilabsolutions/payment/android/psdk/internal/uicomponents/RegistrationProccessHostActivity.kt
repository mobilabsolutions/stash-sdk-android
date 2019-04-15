package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mobilabsolutions.payment.android.R
import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentSdk
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class RegistrationProccessHostActivity : AppCompatActivity() {

    @Inject
    lateinit var uiRequestHandler: UiRequestHandler

    enum class CurrentState {
        CHOOSER, ENTRY
    }

    var currentState = CurrentState.CHOOSER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val injector = NewPaymentSdk.getInjector()
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

    fun setState(state : CurrentState) {
        currentState  = state
    }


}