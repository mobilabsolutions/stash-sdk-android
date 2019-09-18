package com.mobilabsolutions.stash.core.ui.picker

import com.airbnb.epoxy.TypedEpoxyController
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.paymentMethod
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
class PaymentPickerController @Inject constructor(
    private val paymentPickerTextCreator: PaymentPickerTextCreator
) : TypedEpoxyController<PaymentPickerViewState>() {
    var callbacks: Callbacks? = null

    interface Callbacks {
        fun onPaymentMethodClicked(paymentMethodType: PaymentMethodType)
    }

    override fun buildModels(state: PaymentPickerViewState) {
        state.availablePaymentMethods.forEach {
            paymentMethod {
                id(it.name)
                paymentMethodType(it)
                textCreator(paymentPickerTextCreator)
                clickListener { _ -> callbacks?.onPaymentMethodClicked(it) }
            }
        }
    }
}