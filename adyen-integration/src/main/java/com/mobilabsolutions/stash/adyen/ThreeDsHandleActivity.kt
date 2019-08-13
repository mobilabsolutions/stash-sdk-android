package com.mobilabsolutions.stash.adyen

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.adyen.checkout.adyen3ds2.Adyen3DS2Component
import com.adyen.checkout.base.model.payments.response.Threeds2ChallengeAction
import com.adyen.checkout.base.model.payments.response.Threeds2FingerprintAction
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.mobilabsolutions.stash.core.internal.api.backend.MobilabApi
import com.mobilabsolutions.stash.core.internal.api.backend.model.VerifyAliasRequestDto
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
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
                addFlags(FLAG_ACTIVITY_NEW_TASK)
                putExtra(KEY_ACTION, action)
                putExtra(KEY_ALIAS, alias)
            }
        }
    }

    @Inject
    lateinit var adyenHandler: AdyenHandler

    @Inject
    lateinit var mobilabApi: MobilabApi

    override fun onCreate(savedInstanceState: Bundle?) {
        AdyenIntegration.integration?.adyenIntegrationComponent?.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_three_ds)

        val aliasId = intent.getStringExtra(KEY_ALIAS)
        val action = intent.getParcelableExtra<Threeds2FingerprintAction>(KEY_ACTION)
        if (action != null) {
            val threedsComponent = Adyen3DS2Component(application)
            threedsComponent.handleAction(this, action)
            threedsComponent.observe(this, Observer {
                val gson = Gson()
                val jsonString = gson.toJson(it.details)
                val testData: TestData = gson.fromJson(jsonString, TestData::class.java)
                Timber.e("testdata: $testData")

                val challengeResult = testData.nameValuePairs.details.nameValuePairs.challengeResult
                val fingerprint = testData.nameValuePairs.details.nameValuePairs.fingerprint
                if (challengeResult != null) {
                    mobilabApi.verify(
                        aliasId = aliasId,
                        verifyAliasRequest = VerifyAliasRequestDto(
                            challengeResult = challengeResult
                        )
                    ).subscribeOn(Schedulers.io())
                        .subscribe({
                            Timber.e("success: $it")
                            adyenHandler.test(aliasId)
                            finish()
                        }, {
                            Timber.e(it)
                        })
                    return@Observer
                }

                mobilabApi.verify(
                    aliasId = aliasId,
                    verifyAliasRequest = VerifyAliasRequestDto(
                        fingerprintResult = fingerprint
                    )
                ).subscribeOn(Schedulers.io())
                    .subscribe({ response ->
                        Timber.w("test: $response")


                        if (response.actionType == "threeDS2Challenge") {
                            val challengeAction = Threeds2ChallengeAction()
                            challengeAction.token = response.token
                            challengeAction.type = response.actionType
                            challengeAction.paymentData = response.paymentData
                            threedsComponent.handleAction(this, challengeAction)
                        }
                    }, {
                        Timber.e(it)
                    })
            })
        }
    }

    data class TestData(
        val nameValuePairs: NameValuePairs
    )

    data class NameValuePairs(
        val paymentData: String,

        @SerializedName("threeds2.fingerprint")
        val fingerprint: String?,

        @SerializedName("threeds2.challengeResult")
        val challengeResult: String?,
        val details: Details
    )

    data class Details(
        val nameValuePairs: NameValuePairs
    )
}