package com.mobilabsolutions.stash.adyen

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.adyen.checkout.adyen3ds2.Adyen3DS2Component
import com.adyen.checkout.base.model.payments.response.Threeds2FingerprintAction
import timber.log.Timber

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 13-08-2019.
 */
class ThreeDsHandleActivity : AppCompatActivity() {

    companion object {
        private const val KEY_ACTION = "ACTION"

        fun createIntent(context: Context, action: Threeds2FingerprintAction): Intent {
            return Intent(context, ThreeDsHandleActivity::class.java).apply {
                addFlags(FLAG_ACTIVITY_NEW_TASK)
                putExtra(KEY_ACTION, action)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_three_ds)

        val action = intent.getParcelableExtra<Threeds2FingerprintAction>(KEY_ACTION)
        if (action != null) {
            val threedsComponent = Adyen3DS2Component(application)
            if (threedsComponent.canHandleAction(action)) {
                threedsComponent.handleAction(this, action)
                threedsComponent.observe(this, Observer {
                    Timber.e("yeah: $it")
                })
            }
        }
    }
}