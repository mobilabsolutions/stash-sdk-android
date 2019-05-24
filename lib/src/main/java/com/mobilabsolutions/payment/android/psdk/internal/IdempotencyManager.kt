package com.mobilabsolutions.payment.android.psdk.internal

import android.content.SharedPreferences
import android.text.format.DateUtils
import com.google.gson.Gson
import com.mobilabsolutions.payment.android.psdk.PaymentMethodAlias
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.exceptions.validation.IdempotencyKeyInUseException
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named

class IdempotencyManager @Inject constructor(
    private val gson: Gson,
    @Named("idempotency") private val sharedPreferences: SharedPreferences
) {
    init {
        sharedPreferences.all
            .mapValues { gson.fromJson(it.value.toString(), IdempotencyData::class.java) }
            .filterValues { !DateUtils.isToday(it.timestamp) }
            .keys
            .forEach {
                sharedPreferences.edit()
                    .remove(it)
                    .apply()
            }
    }

    // @SuppressLint("LogNotTimber")
    private fun Single<PaymentMethodAlias>.persistResult(idempotencyKey: String, paymentMethodType: PaymentMethodType): Single<PaymentMethodAlias> {
        return doOnEvent { result, error ->
            // Log.d("IdempotencyManager","Saving (Result: $result)\\(Error: $error)")
            sharedPreferences.edit()
                .putString(idempotencyKey, gson.toJson(IdempotencyData(System.currentTimeMillis(), paymentMethodType, result, error)))
                .apply()
        }
    }

    internal fun verifyIdempotencyAndContinue(idempotencyKey: String, paymentMethodType: PaymentMethodType, function: () -> Single<PaymentMethodAlias>): Single<PaymentMethodAlias> {
        sharedPreferences.getString(idempotencyKey, null)?.let {
            val idempotencyData = gson.fromJson(it, IdempotencyData::class.java)
            // Check for the same Idempotency key Used for a different Payment method
            return if (idempotencyData.paymentMethodType != paymentMethodType) {
                Single.error(IdempotencyKeyInUseException())
            } else when {
                idempotencyData.paymentMethodAlias != null -> Single.just(idempotencyData.paymentMethodAlias)
                else -> Single.error(idempotencyData.error)
            }
        } ?: run {
            return function()
                .persistResult(idempotencyKey, paymentMethodType)
        }
    }
}