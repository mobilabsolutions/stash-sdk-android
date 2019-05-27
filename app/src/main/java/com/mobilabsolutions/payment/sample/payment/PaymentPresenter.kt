package com.mobilabsolutions.payment.sample.payment

import com.mobilabsolutions.commonsv3.mvp.presenter.CommonPresenter
import com.mobilabsolutions.payment.android.psdk.exceptions.base.BasePaymentException
import com.mobilabsolutions.payment.android.psdk.exceptions.base.TemporaryException
import com.mobilabsolutions.payment.android.psdk.exceptions.payment.PaymentFailedException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PaymentPresenter : CommonPresenter<PaymentView>() {

    lateinit var paymentViewState: PaymentViewState

    @Inject
    lateinit var paymentController: PaymentController

    val compositeDisposable = CompositeDisposable()

    var paymentMethodMap: Map<String, String> = HashMap()

    override fun onInitialized() {
        super.onInitialized()
        paymentController.getPaymentMethodObservable().subscribeBy(
                onNext = {
                    paymentViewState = PaymentViewState.DataState(paymentMethods = it.keys.toList())
                    paymentMethodMap = it
                    applyOnUi { it.render(paymentViewState) }
                },
                onError = {
                    paymentViewState = PaymentViewState.ErrorState("Failed: ${it.message}")
                }
        )
    }

    override fun onUiVisible() {
        super.onUiVisible()
        applyOnUi { ui ->
            ui.amountObservable.subscribeBy(onNext = { Timber.d("Got amount $it") })
            ui.currencyObservable.subscribeBy(onNext = { Timber.d("Got currency $it") })
            ui.paymentMethodObservable.subscribeBy(onNext = { Timber.d("Got payment $it") })
            ui.reasonObservable.subscribeBy(onNext = { Timber.d("Got reason $it") })

            compositeDisposable.add(Observables.combineLatest(
                    ui.amountObservable,
                    ui.currencyObservable,
                    ui.paymentMethodObservable,
                    ui.reasonObservable
            ) { amount: String, currency: String, paymentMethod: String, reason: String ->
                Timber.d("Got $amount, $currency, $paymentMethod, $reason")
            }.subscribe()
            )
        }
    }

    fun paymentExecutionRequested() {
        applyOnUi { ui ->
            compositeDisposable.add(Observables.combineLatest(
                    ui.amountObservable,
                    ui.currencyObservable,
                    ui.paymentMethodObservable,
                    ui.reasonObservable
            ) { amount: String, currency: String, paymentMethod: String, reason: String ->
                Timber.d("Executing payment $reason")
                paymentController.executePayment(paymentMethodMap[paymentMethod]!!, amount.toInt(), reason, currency)
            }.take(1).flatMapSingle { it }

                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onNext = {
                                Timber.d("Payment success")
                                ui.render(PaymentViewState.PaymentSuccess(it))
                            },
                            onError = {
                                if (it is BasePaymentException) {
                                    when (it) {
                                        is UnknownError -> ui.render(PaymentViewState.ErrorState(it.message))
                                        is PaymentFailedException -> ui.render(PaymentViewState.ErrorState("Payment failed!"))
                                        is TemporaryException -> ui.render(PaymentViewState.ErrorState(it.message))
                                        else -> ui.render(PaymentViewState.ErrorState("Unexpected throwable! ${it.message}"))
                                    }
                                }
                                ui.render(PaymentViewState.ErrorState(it.message!!))
                            }
                    )
            )
        }
    }
}