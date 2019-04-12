package com.mobilabsolutions.payment.sample.main.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.payment.sample.core.BaseFragment
import com.mobilabsolutions.payment.sample.databinding.FragmentCheckoutBinding
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class CheckoutFragment : BaseFragment() {
    @Inject
    lateinit var checkoutViewModelFactory: CheckoutViewModel.Factory

    private val viewModel: CheckoutViewModel by fragmentViewModel()
    private lateinit var binding: FragmentCheckoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun invalidate() {
        withState(viewModel) {

        }
    }
}
