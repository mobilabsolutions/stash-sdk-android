package com.mobilabsolutions.payment.android.psdk.integration.bspayone.uicomponents

import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.R
import com.mobilabsolutions.payment.android.psdk.internal.IntegrationScope
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import java.lang.RuntimeException
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
class UiComponentHandler @Inject constructor() {

    var dataSubject: PublishSubject<Map<String, String>> = PublishSubject.create()

    fun handleSepaDataEntryRequest(activity: AppCompatActivity): Single<Map<String, String>> {
        dataSubject = PublishSubject.create()
        val sepaDataEntryFragment = SepaDataEntryFragment()
        activity.supportFragmentManager.beginTransaction().add(R.id.host_activity_fragment, sepaDataEntryFragment).commitNow()
        return dataSubject.doFinally {
            activity.supportFragmentManager.beginTransaction().remove(sepaDataEntryFragment).commitNow()
        }.firstOrError()
    }


    fun handleCreditCardDataEntryRequest(activity: AppCompatActivity): Single<Map<String, String>> {
        dataSubject = PublishSubject.create()
        val creditCardDataEntryFragment = CreditCardDataEntryFragment()
        activity.supportFragmentManager.beginTransaction().add(R.id.host_activity_fragment, creditCardDataEntryFragment).commitNow()
        return dataSubject.doFinally {
            activity.supportFragmentManager.beginTransaction().remove(creditCardDataEntryFragment).commitNow()
        }.firstOrError()
    }

}