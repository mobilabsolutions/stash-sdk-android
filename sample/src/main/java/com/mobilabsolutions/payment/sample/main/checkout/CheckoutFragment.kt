package com.mobilabsolutions.payment.sample.main.checkout

import com.mobilabsolutions.payment.sample.core.BaseFragment
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class CheckoutFragment : BaseFragment() {
    @Inject
    lateinit var checkoutViewModelFactory: CheckoutViewModel.Factory

    override fun invalidate() {

    }
}