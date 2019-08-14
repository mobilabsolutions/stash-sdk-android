package com.mobilabsolutions.stash.adyen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.adyen.checkout.adyen3ds2.Adyen3DS2Component
import com.adyen.checkout.base.model.payments.response.Threeds2ChallengeAction
import com.adyen.checkout.base.model.payments.response.Threeds2FingerprintAction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 13-08-2019.
 */
class ThreeDsHandleActivity : AppCompatActivity() {
    companion object {
        private const val KEY_ACTION = "ACTION"
        private const val KEY_ALIAS = "ALIAS"

        fun createIntent(context: Context, action: Threeds2FingerprintAction, alias: String): Intent {
            return Intent(context, ThreeDsHandleActivity::class.java).apply {
                putExtra(KEY_ACTION, action)
                putExtra(KEY_ALIAS, alias)
            }
        }
    }

    @Inject
    lateinit var adyenHandler: AdyenHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        AdyenIntegration.integration?.adyenIntegrationComponent?.inject(this)
        super.onCreate(savedInstanceState)

        val aliasId = intent.getStringExtra(KEY_ALIAS)
        val action = intent.getParcelableExtra<Threeds2FingerprintAction>(KEY_ACTION)
        if (action != null) {
            val threedsComponent = Adyen3DS2Component(application)
            threedsComponent.handleAction(this, action)
            threedsComponent.observeErrors(this, Observer {
                adyenHandler.onThreeDsError(this, it.exception)
            })
            threedsComponent.observe(this, Observer {
                adyenHandler.handleAdyenThreeDsResult(this, it, aliasId)
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            if (response.actionType == "threeDS2Challenge") {
                                val challengeAction = Threeds2ChallengeAction().apply {
                                    token = response.token
                                    type = response.actionType
                                    paymentData = response.paymentData
                                }
                                threedsComponent.handleAction(this, challengeAction)
                            }
                        }, {

                        })
            })
        }
    }
}