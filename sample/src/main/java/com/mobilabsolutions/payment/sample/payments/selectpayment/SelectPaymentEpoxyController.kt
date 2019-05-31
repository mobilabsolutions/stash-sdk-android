package com.mobilabsolutions.payment.sample.payments.selectpayment

import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.selectPaymentItem

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class SelectPaymentEpoxyController(
    private val callbacks: Callbacks
) : TypedEpoxyController<SelectPaymentViewState>() {

    private var lastSelectedView: View? = null

    interface Callbacks {
        fun onSelection(paymentMethod: PaymentMethod)
    }

    override fun buildModels(state: SelectPaymentViewState) {
        state.paymentMethods.forEach {
            selectPaymentItem {
                id(it.id)
                paymentMethod(it)
                selectListener { view ->
                    callbacks.onSelection(it)
                    updateSelection(view)
                }
            }
        }
    }

    private fun updateSelection(view: View) {
        view.isSelected = true
        if (lastSelectedView !== view) {
            lastSelectedView?.let {
                it.isSelected = false
            }
            lastSelectedView = view
        }
    }
}