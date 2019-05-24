package com.mobilabsolutions.payment.sample.payments.selectpayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.payment.sample.core.BaseFragment
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.databinding.FragmentSelectPaymentBinding
import javax.inject.Inject

class SelectPaymentFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: SelectPaymentViewModel.Factory

    private val viewModel: SelectPaymentViewModel by fragmentViewModel()
    private lateinit var binding: FragmentSelectPaymentBinding
    private lateinit var controller: SelectPaymentEpoxyController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSelectPaymentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = SelectPaymentEpoxyController(object : SelectPaymentEpoxyController.Callbacks {
            override fun onSelection(paymentMethod: PaymentMethod) {
                viewModel.onSelection()
            }
        })
        binding.paymentMethodsRv.setController(controller)
        binding.back.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    companion object {

        fun newInstance(amount: Int): SelectPaymentFragment {
            val fragment = SelectPaymentFragment()
            val args = Bundle()
            args.putInt("amount", amount)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setAmount(arguments?.getInt("amount", 0) ?: 0)
    }

    override fun invalidate() {
        withState(viewModel) {
            binding.state = it
            controller.setData(it)
        }
    }
}