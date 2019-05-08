package com.mobilabsolutions.payment.android.util

import android.content.Context
import android.telephony.TelephonyManager

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
object CountryDetectorUtil {
    fun getBestGuessAtCurrentCountry(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountry = telephonyManager.simCountryIso
        return simCountry
    }
}