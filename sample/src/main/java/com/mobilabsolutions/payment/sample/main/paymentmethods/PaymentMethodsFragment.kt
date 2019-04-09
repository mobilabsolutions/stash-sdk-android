package com.mobilabsolutions.payment.sample.main.paymentmethods

import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.payment.sample.core.BaseFragment
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
class PaymentMethodsFragment : BaseFragment() {
    @Inject
    lateinit var paymentMethodsViewModelFactory: PaymentMethodsViewModel.Factory

    private val viewModel: PaymentMethodsViewModel by fragmentViewModel()

    override fun invalidate() {
        withState(viewModel) {

        }
    }
}