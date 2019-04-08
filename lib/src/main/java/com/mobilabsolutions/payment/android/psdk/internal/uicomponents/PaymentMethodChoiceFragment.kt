package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobilabsolutions.payment.android.R
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentSdk
import kotlinx.android.synthetic.main.payment_method_chooser_fragment.*
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PaymentMethodChoiceFragment : Fragment() {

    @Inject
    lateinit var uiRequestHandler: UiRequestHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.payment_method_chooser_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testButton.setOnClickListener {
            println("Chosen")
            uiRequestHandler.paymentMethodTypeSubject.onNext(PaymentMethodType.CREDITCARD)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NewPaymentSdk.getInjector().inject(this)
    }
}