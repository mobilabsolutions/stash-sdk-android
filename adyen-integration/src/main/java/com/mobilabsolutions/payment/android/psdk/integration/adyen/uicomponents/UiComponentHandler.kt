package com.mobilabsolutions.payment.android.psdk.integration.adyen.uicomponents

import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.psdk.integration.adyen.R
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationScope
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.UiRequestHandler
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
class UiComponentHandler @Inject constructor() {

    private var dataSubject: PublishSubject<AdditionalRegistrationData> = PublishSubject.create()

    private var resultObservable: Observable<UiRequestHandler.DataEntryResult>? = null

    fun submitData(data: Map<String, String>) {
        dataSubject.onNext(AdditionalRegistrationData(data))
    }

    fun getResultObservable(): Observable<UiRequestHandler.DataEntryResult> {
        return resultObservable!!
    }

    fun handleSepaDataEntryRequest(activity: AppCompatActivity, resultObservable: Observable<UiRequestHandler.DataEntryResult>): Observable<AdditionalRegistrationData> {
        dataSubject = PublishSubject.create()
        val sepaDataEntryFragment = AdyenSepaDataEntryFragment()
        this.resultObservable = resultObservable.doOnNext {
            if (it is UiRequestHandler.DataEntryResult.Success) {
                activity.supportFragmentManager.beginTransaction().remove(sepaDataEntryFragment).commitNow()
            }
        }
        Timber.d("Current thread: ${Thread.currentThread().name}")
        activity.supportFragmentManager.beginTransaction().add(R.id.host_activity_fragment, sepaDataEntryFragment).commitNow()
        return dataSubject
    }

    fun handleCreditCardDataEntryRequest(activity: AppCompatActivity, resultObservable: Observable<UiRequestHandler.DataEntryResult>): Observable<AdditionalRegistrationData> {
        dataSubject = PublishSubject.create()
        val creditCardDataEntryFragment = AdyenCreditCardDataEntryFragment()
        this.resultObservable = resultObservable.doOnNext {
            if (it is UiRequestHandler.DataEntryResult.Success) {
                activity.supportFragmentManager.beginTransaction().remove(creditCardDataEntryFragment).commitNow()
            }
        }
        activity.supportFragmentManager.beginTransaction().add(R.id.host_activity_fragment, creditCardDataEntryFragment).commitNow()
        return dataSubject
    }
}