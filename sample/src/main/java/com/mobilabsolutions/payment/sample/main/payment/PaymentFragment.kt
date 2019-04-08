package com.mobilabsolutions.payment.sample.main.payment

import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.payment.sample.core.BaseFragment
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
class PaymentFragment : BaseFragment() {
    @Inject
    lateinit var paymentViewModelFactory: PaymentViewModel.Factory

    private val viewModel: PaymentViewModel by fragmentViewModel()

    override fun invalidate() {
        withState(viewModel) {

        }
    }
}