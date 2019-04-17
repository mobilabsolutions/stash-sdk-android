package com.mobilabsolutions.payment.sample.main.paymentmethods

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.payment.sample.R
import com.mobilabsolutions.payment.sample.core.BaseFragment
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.databinding.FragmentPaymentMethodsBinding
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
class PaymentMethodsFragment : BaseFragment() {
    override val mvrxViewId: String = PaymentMethodsFragment::class.java.simpleName

    @Inject
    lateinit var paymentMethodsViewModelFactory: PaymentMethodsViewModel.Factory

    private val viewModel: PaymentMethodsViewModel by fragmentViewModel()
    private lateinit var binding: FragmentPaymentMethodsBinding
    private lateinit var controller: PaymentMethodsEpoxyController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPaymentMethodsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = PaymentMethodsEpoxyController(object : PaymentMethodsEpoxyController.Callbacks {
            override fun onAddBtnClicked() {
                viewModel.onAddBtnClicked()
            }

            override fun onDeleteBtnClicked(paymentMethod: PaymentMethod) {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle(getString(R.string.delete_payment_method_title))
                dialog.setMessage(getString(R.string.delete_payment_method_message))
                dialog.setPositiveButton("Yes") { view, _ ->
                    view.dismiss()
                    viewModel.onDeleteBtnClicked(paymentMethod)
                }
                dialog.setNegativeButton("No") { view, _ -> view.dismiss() }
                dialog.show()
            }
        })
        binding.paymentMethodsRv.setController(controller)
    }

    override fun invalidate() {
        withState(viewModel) {
            binding.state = it
            controller.setData(it)
        }
    }
}