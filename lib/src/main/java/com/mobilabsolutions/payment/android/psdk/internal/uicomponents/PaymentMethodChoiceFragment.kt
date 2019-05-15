package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobilabsolutions.payment.android.R
import com.mobilabsolutions.payment.android.psdk.CustomizationExtensions
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.CustomizationPreference
import com.mobilabsolutions.payment.android.psdk.internal.NewPaymentSdk
import com.mobilabsolutions.payment.android.psdk.UiCustomizationManager
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.payment_method_chooser_fragment.*
import kotlinx.android.synthetic.main.payment_method_entry.view.*
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PaymentMethodChoiceFragment : Fragment() {

    @Inject
    lateinit var uiRequestHandler: UiRequestHandler

    @Inject
    lateinit var uiCustomizationManager: UiCustomizationManager

    lateinit var customizationPreference: CustomizationPreference

    private lateinit var paymentMethodAdapter: PaymentMethodAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.payment_method_chooser_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customizationPreference = uiCustomizationManager.getCustomizationPreferences()
        paymentMethodAdapter = PaymentMethodAdapter(
                uiRequestHandler.availablePaymentMethods(),
                uiRequestHandler.paymentMethodTypeSubject,
                customizationPreference
        )
        CustomizationExtensions {
            paymentMethodChooserRootLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), customizationPreference.backgroundColor))
            titleTextView.applyTextCustomization(customizationPreference)
            explanationTextView.applyTextCustomization(customizationPreference)
        }

        uiRequestHandler.availablePaymentMethods().forEach { Timber.d("Method: ${it.paymentMethodType.name}") }

        paymentMethodRecyclerView.layoutManager = LinearLayoutManager(context)
        paymentMethodRecyclerView.adapter = paymentMethodAdapter

        cancelImageView.setOnClickListener {
            activity?.onBackPressed()
        }
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
        val rootView = view.paymentMethodRootView
        val paymentMethodName = view.paymentMethodTypeTextView
        val paymentMethodIcon = view.paymentMethodIconImageView
    }

    class PaymentMethodAdapter(
        val availablePaymentMethods: List<PaymentMethodDefinition>,
        val paymentMethodSubject: ReplaySubject<PaymentMethodType>,
        val customizationPreference: CustomizationPreference
    ) : RecyclerView.Adapter<PaymentMethodViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
            val holder = PaymentMethodViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.payment_method_entry, parent, false))
            return holder
        }

        override fun getItemCount(): Int {
            return availablePaymentMethods.size
        }

        override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
            val paymentMethodDefinition = availablePaymentMethods[position]
            holder.paymentMethodName.setText(
                    when (paymentMethodDefinition.paymentMethodType) {
                        PaymentMethodType.CC -> R.string.payment_chooser_credit_card
                        PaymentMethodType.SEPA -> R.string.payment_chooser_sepa
                        PaymentMethodType.PAYPAL -> R.string.payment_chooser_paypal
                    }
            )
            holder.paymentMethodIcon.setImageResource(
                    when (paymentMethodDefinition.paymentMethodType) {
                        PaymentMethodType.CC -> R.drawable.ic_credit
                        PaymentMethodType.SEPA -> R.drawable.ic_sepa_symbol
                        PaymentMethodType.PAYPAL -> R.drawable.ic_paypal_grey
                    }
            )
            holder.rootView.setOnClickListener {
                paymentMethodSubject.onNext(paymentMethodDefinition.paymentMethodType)
            }
            CustomizationExtensions {
                holder.paymentMethodName.applyTextCustomization(customizationPreference)
                holder.rootView.setBackgroundColor(ContextCompat.getColor(holder.rootView.context, customizationPreference.cellBackgroundColor))
            }
        }
    }
}
