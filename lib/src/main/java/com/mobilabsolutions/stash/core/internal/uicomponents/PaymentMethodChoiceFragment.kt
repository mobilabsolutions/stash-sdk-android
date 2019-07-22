/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.uicomponents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobilabsolutions.stash.core.R
import com.mobilabsolutions.stash.core.CustomizationExtensions
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.PaymentUiConfiguration
import com.mobilabsolutions.stash.core.UiCustomizationManager
import com.mobilabsolutions.stash.core.internal.StashImpl
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.payment_method_chooser_fragment.cancelImageView
import kotlinx.android.synthetic.main.payment_method_chooser_fragment.explanationTextView
import kotlinx.android.synthetic.main.payment_method_chooser_fragment.paymentMethodChooserRootLayout
import kotlinx.android.synthetic.main.payment_method_chooser_fragment.paymentMethodRecyclerView
import kotlinx.android.synthetic.main.payment_method_chooser_fragment.titleTextView
import kotlinx.android.synthetic.main.payment_method_entry.view.paymentMethodIconImageView
import kotlinx.android.synthetic.main.payment_method_entry.view.paymentMethodRootView
import kotlinx.android.synthetic.main.payment_method_entry.view.paymentMethodTypeTextView
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

    lateinit var paymentUIConfiguration: PaymentUiConfiguration

    private lateinit var paymentMethodAdapter: PaymentMethodAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.payment_method_chooser_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentUIConfiguration = uiCustomizationManager.getCustomizationPreferences()
        paymentMethodAdapter = PaymentMethodAdapter(
            uiRequestHandler.availablePaymentMethods(),
            uiRequestHandler.paymentMethodTypeSubject,
            paymentUIConfiguration
        )
        CustomizationExtensions {
            paymentMethodChooserRootLayout.applyBackgroundCustomization(paymentUIConfiguration)
            titleTextView.applyTextCustomization(paymentUIConfiguration)
            explanationTextView.applyTextCustomization(paymentUIConfiguration)
        }

        uiRequestHandler.availablePaymentMethods().forEach { Timber.d("Method: ${it.name}") }

        paymentMethodRecyclerView.layoutManager = LinearLayoutManager(context)
        paymentMethodRecyclerView.adapter = paymentMethodAdapter

        cancelImageView.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StashImpl.getInjector().inject(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!uiRequestHandler.paymentMethodTypeSubject.hasValue()) {
            uiRequestHandler.paymentMethodTypeSubject.onError(UiRequestHandler.UserCancelled())
        }
    }

    class PaymentMethodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView = view.paymentMethodRootView
        val paymentMethodName = view.paymentMethodTypeTextView
        val paymentMethodIcon = view.paymentMethodIconImageView
    }

    class PaymentMethodAdapter(
        val availablePaymentMethods: List<PaymentMethodType>,
        val paymentMethodSubject: ReplaySubject<PaymentMethodType>,
        val paymentUIConfiguration: PaymentUiConfiguration
    ) : RecyclerView.Adapter<PaymentMethodViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
            return PaymentMethodViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.payment_method_entry, parent, false))
        }

        override fun getItemCount(): Int {
            return availablePaymentMethods.size
        }

        override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
            val paymentMethodType = availablePaymentMethods[position]
            holder.paymentMethodName.setText(
                when (paymentMethodType) {
                    PaymentMethodType.CC -> R.string.payment_chooser_credit_card
                    PaymentMethodType.SEPA -> R.string.payment_chooser_sepa
                    PaymentMethodType.PAYPAL -> R.string.payment_chooser_paypal
                }
            )
            holder.paymentMethodIcon.setImageResource(
                when (paymentMethodType) {
                    PaymentMethodType.CC -> R.drawable.ic_credit
                    PaymentMethodType.SEPA -> R.drawable.ic_sepa_symbol
                    PaymentMethodType.PAYPAL -> R.drawable.ic_paypal_grey
                }
            )
            holder.rootView.setOnClickListener {
                paymentMethodSubject.onNext(paymentMethodType)
            }
            CustomizationExtensions {
                holder.paymentMethodName.applyTextCustomization(paymentUIConfiguration)
                holder.rootView.applyCellBackgroundCustomization(paymentUIConfiguration)
            }
        }
    }
}
