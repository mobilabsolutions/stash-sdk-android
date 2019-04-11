package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.R
import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentSdk
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class RegistrationProccessHostActivity : AppCompatActivity() {

    @Inject
    lateinit var uiRequestHandler: UiRequestHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val injector = NewPaymentSdk.getInjector()
        injector.inject(this)
        uiRequestHandler.provideHostActivity(this)
        setContentView(R.layout.registration_host_activity)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        uiRequestHandler.hostActivityDismissed()
    }
}