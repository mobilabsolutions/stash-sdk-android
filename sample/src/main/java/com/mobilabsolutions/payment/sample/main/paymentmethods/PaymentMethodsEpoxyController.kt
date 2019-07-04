/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.payment.sample.main.paymentmethods

import com.airbnb.epoxy.TypedEpoxyController
import com.mobilabsolutions.payment.sample.addPayment
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.paymentMethodItem

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class PaymentMethodsEpoxyController(
    private val callbacks: Callbacks
) : TypedEpoxyController<PaymentMethodsViewState>() {

    interface Callbacks {
        fun onAddBtnClicked()
        fun onDeleteBtnClicked(paymentMethod: PaymentMethod)
    }

    override fun buildModels(state: PaymentMethodsViewState) {
        state.paymentMethods.forEach {
            paymentMethodItem {
                id(it.id)
                paymentMethod(it)
                deleteBtnClickListener { _ -> callbacks.onDeleteBtnClicked(it) }
            }
        }
        addPayment {
            id("add_payment_view")
            clickListener { _ -> callbacks.onAddBtnClicked() }
        }
    }
}