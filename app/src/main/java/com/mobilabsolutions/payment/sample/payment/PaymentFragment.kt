package com.mobilabsolutions.payment.sample.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.SimpleAdapter
import com.mobilabsolutions.commonsv3.mvp.view.Ui
import com.mobilabsolutions.commonsv3_dagger.mvp.view.DaggerCommonFragment
import com.mobilabsolutions.payment.sample.R
import com.mobilabsolutions.payment.sample.getStringObservable
import io.reactivex.Observable
import kotlinx.android.synthetic.main.payment_fragment.*
import timber.log.Timber
import java.util.*

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface PaymentView : Ui {
    var amountObservable : Observable<String>

    var reasonObservable : Observable<String>

    var currencyObservable : Observable<String>

    var paymentMethodObservable : Observable<String>

    fun render(state : PaymentViewState)
}

class PaymentFragment : DaggerCommonFragment<PaymentPresenter>(), PaymentView {
    override val presenterType: Class<PaymentPresenter> = PaymentPresenter::class.java


    lateinit var spinnerAdapter : ArrayAdapter<String>

    lateinit var spinnerObservable : Observable<String>

    override lateinit var amountObservable: Observable<String>

    override lateinit var reasonObservable: Observable<String>

    override lateinit var currencyObservable: Observable<String>

    override lateinit var paymentMethodObservable: Observable<String>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.payment_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        amountObservable = amountEditText.getStringObservable().replay(1).autoConnect()
        reasonObservable = reasonEditText.getStringObservable().replay(1).autoConnect()
        currencyObservable = currencyEditText.getStringObservable().replay(1).autoConnect()
        spinnerObservable = Observable.create {
            paymentMethodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Timber.d("Nothing")
                    it.onNext("")
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    Timber.d("Position: $position")
                    it.onNext(spinnerAdapter.getItem(position))
                }

            }
        }
        paymentMethodObservable = spinnerObservable.replay(1).autoConnect()
        spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, LinkedList())
        paymentMethodSpinner.adapter = spinnerAdapter



        executePaymentButton.setOnClickListener {
            notifyPresenter { it.paymentExecutionRequested() }
        }

    }



    override fun render(state: PaymentViewState) {
        when (state) {
            is PaymentViewState.DataState -> {
                spinnerAdapter.clear()
                spinnerAdapter.addAll(state.paymentMethods)
            }
            is PaymentViewState.ErrorState -> {
                statusTextView.text = "Failed ${state.error}"
            }
            is PaymentViewState.LoadingMethods -> {
                statusTextView.text = "Loading payment methods"
            }
            is PaymentViewState.ExecutingPayment -> {
                statusTextView.text = "Executing payment"
            }
            is PaymentViewState.PaymentSuccess -> {
                statusTextView.text = "Payment success! ${state.transactionId}"
            }
        }
    }


}