package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobilabsolutions.payment.android.R
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentSdk
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.payment_method_chooser_fragment.*
import kotlinx.android.synthetic.main.payment_method_entry.view.*
import timber.log.Timber
import java.lang.RuntimeException
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PaymentMethodChoiceFragment : Fragment() {

    @Inject
    lateinit var uiRequestHandler: UiRequestHandler

    private lateinit var paymentMethodAdapter: PaymentMethodAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.payment_method_chooser_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentMethodAdapter = PaymentMethodAdapter(
                uiRequestHandler.availablePaymentMethods(),
                uiRequestHandler.paymentMethodTypeSubject
        )
        uiRequestHandler.availablePaymentMethods().forEach { Timber.d("Method: ${it.paymentMethodType.name}") }

        paymentMethodRecyclerView.layoutManager = LinearLayoutManager(context)
        paymentMethodRecyclerView.adapter = paymentMethodAdapter

//        sepaButton.setOnClickListener {
//            choiceMade = true
//            uiRequestHandler.paymentMethodTypeSubject.onNext(PaymentMethodType.SEPA)
//        }
//        ccTmpButton.setOnClickListener {
//            choiceMade = true
//            uiRequestHandler.paymentMethodTypeSubject.onNext(PaymentMethodType.CREDITCARD)
//        }
//        paypalButton.setOnClickListener {
//            choiceMade = true
//            uiRequestHandler.paymentMethodTypeSubject.onNext(PaymentMethodType.PAYPAL)
//        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NewPaymentSdk.getInjector().inject(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!uiRequestHandler.paymentMethodTypeSubject.hasValue()) {
            uiRequestHandler.paymentMethodTypeSubject.onError(RuntimeException("User canceled choosing method"))
        }
    }

    class PaymentMethodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val paymentMethodName = view.paymentMethodTypeTextView
    }

    class PaymentMethodAdapter(val availablePaymentMethods: List<PaymentMethodDefinition>, val paymentMethodSubject: ReplaySubject<PaymentMethodType>) : RecyclerView.Adapter<PaymentMethodViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
            val holder = PaymentMethodViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.payment_method_entry, parent, false))
            return holder
        }

        override fun getItemCount(): Int {
            return availablePaymentMethods.size
        }

        override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
            holder.paymentMethodName.text = availablePaymentMethods[position].paymentMethodType.name
            holder.paymentMethodName.setOnClickListener {
                paymentMethodSubject.onNext(availablePaymentMethods[position].paymentMethodType)

            }

        }

    }
}

