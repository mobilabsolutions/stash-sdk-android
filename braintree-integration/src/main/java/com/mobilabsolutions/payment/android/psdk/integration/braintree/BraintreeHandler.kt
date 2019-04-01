package com.mobilabsolutions.payment.android.psdk.integration.braintree

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationScope
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.lang.RuntimeException
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
class BraintreeHandler @Inject constructor(){

    internal val processing = AtomicBoolean(false)
    internal var resultSubject = PublishSubject.create<String>()


    fun tokenizePaymentMethods(activity: AppCompatActivity) : Single<String> {
        return if (processing.compareAndSet(false,true)) {
            resultSubject = PublishSubject.create()
            val payPalActivityIntent = Intent(activity, BraintreePayPalActivity::class.java)
            payPalActivityIntent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(payPalActivityIntent)
            resultSubject
                    .doOnEach {
                        Timber.d("Event from paypal activity $it")
                    }
                    .doFinally {
                        Timber.d("Finalizing")
                        processing.set(false)
                    }
                    .firstOrError()
        } else {
            Single.error(RuntimeException("Braintree PayPal activity already shown!"))
        }


    }

}