package com.mobilabsolutions.payment.sample.payment

import com.mobilabsolutions.commonsv3.mvp.controller.Controller
import com.mobilabsolutions.payment.android.psdk.PaymentManager
import com.mobilabsolutions.payment.android.psdk.model.PaymentData
import com.mobilabsolutions.payment.sample.state.PaymentMethodState
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Singleton
class PaymentController @Inject constructor() : Controller() {

    @Inject
    lateinit var paymentManager: PaymentManager


    @Inject
    lateinit var paymentMethodStateSubject : BehaviorSubject<PaymentMethodState>

    fun getPaymentMethodObservable() = paymentMethodStateSubject.map { it.paymentMethodMap }

    fun executePayment(paymentMethod : String, amount : Int, reason : String, currency : String) : Single<String> {
        return paymentManager.executeCreditCardPaymentWithAlias(paymentMethod,
                PaymentData(
                        amount = amount,
                        reason = reason,
                        currency = currency
                )
        )
    }

}