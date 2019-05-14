package com.mobilabsolutions.payment.sample.payments.selectpayment

import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.selectPaymentItem
import kotlinx.coroutines.selects.select
import timber.log.Timber


/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class SelectPaymentEpoxyController(
    private val callbacks: Callbacks
) : TypedEpoxyController<SelectPaymentViewState>() {

    interface Callbacks {
        fun onSelection(paymentMethod: PaymentMethod)
    }

    override fun buildModels(state: SelectPaymentViewState) {
        state.paymentMethods.forEach {
            selectPaymentItem {
                id(it.id)
                paymentMethod(it)
                selectListener { _ ->
                    callbacks.onSelection(it)
                    Timber.d("XXX ${it.type}")
                }
            }
        }
    }
}