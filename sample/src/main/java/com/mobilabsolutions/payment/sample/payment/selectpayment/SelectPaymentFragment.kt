package com.mobilabsolutions.payment.sample.payment.selectpayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.payment.sample.core.BaseFragment
import com.mobilabsolutions.payment.sample.databinding.FragmentSelectPaymentBinding
import javax.inject.Inject

class SelectPaymentFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: SelectPaymentViewModel.Factory

    private val viewModel: SelectPaymentViewModel by fragmentViewModel()
    private lateinit var binding: FragmentSelectPaymentBinding
    //private lateinit var controller: PaymentMethodsEpoxyController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSelectPaymentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        controller = PaymentMethodsEpoxyController(object : PaymentMethodsEpoxyController.Callbacks {
//            override fun onAddBtnClicked() {
//                viewModel.onAddBtnClicked()
//            }
//
//            override fun onDeleteBtnClicked(paymentMethod: PaymentMethod) {
//                val dialog = AlertDialog.Builder(requireContext())
//                dialog.setTitle(getString(R.string.delete_payment_method_title))
//                dialog.setMessage(getString(R.string.delete_payment_method_message))
//                dialog.setPositiveButton("Yes") { view, _ ->
//                    view.dismiss()
//                    viewModel.onDeleteBtnClicked(paymentMethod)
//                }
//                dialog.setNegativeButton("No") { view, _ -> view.dismiss() }
//                dialog.show()
//            }
//        })
//        binding.paymentMethodsRv.setController(controller)
    }

    override fun invalidate() {
        withState(viewModel) {
            binding.state = it
            // controller.setData(it)
        }
    }
}