package com.mobilabsolutions.payment.android.psdk.integration.braintree

import android.content.Context
import android.content.Intent
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

    @Inject
    lateinit var context : Context

    internal val processing = AtomicBoolean(false)
    internal var resultSubject = PublishSubject.create<String>()


    fun tokenizePaymentMethods() : Single<String> {
        return if (processing.compareAndSet(false,true)) {
            resultSubject = PublishSubject.create()
            val payPalActivityIntent = Intent(context, BraintreePayPalActivity::class.java)
            payPalActivityIntent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(payPalActivityIntent)
            resultSubject
                    .doOnComplete {
                        Timber.d("Nonce subject completed")
                        processing.set(false)
                    }
                    .doOnError { processing.set(false) }
                    .firstOrError()
        } else {
            Single.error(RuntimeException("TODO update me!")) //TODO
        }


    }

}