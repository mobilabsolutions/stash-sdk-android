package com.mobilabsolutions.payment.sample.main.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.payment.sample.core.BaseFragment
import com.mobilabsolutions.payment.sample.data.resultentities.CartWithProduct
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
    private lateinit var controller: CheckoutEpoxyController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = CheckoutEpoxyController(object : CheckoutEpoxyController.Callbacks {
            override fun onAddButtonClicked(cartWithProduct: CartWithProduct) {
                viewModel.onAddButtonClicked(cartWithProduct)
            }

            override fun onRemoveButtonClicked(cartWithProduct: CartWithProduct) {
                viewModel.onRemoveButtonClicked(cartWithProduct)
            }
        })
        binding.checkoutRv.setController(controller)
        binding.btnPay.setOnClickListener { viewModel.onPayBtnClicked() }
    }

    override fun invalidate() {
        withState(viewModel) {
            binding.state = it
            controller.setData(it)
        }
    }
}