package com.mobilabsolutions.payment.sample.main.paymentmethods

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.snackbar.Snackbar
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.SnackBarExtensions
import com.mobilabsolutions.payment.sample.R
import com.mobilabsolutions.payment.sample.core.BaseFragment
import com.mobilabsolutions.payment.sample.data.entities.PaymentMethod
import com.mobilabsolutions.payment.sample.databinding.FragmentPaymentMethodsBinding
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
class PaymentMethodsFragment : BaseFragment() {
    @Inject
    lateinit var paymentMethodsViewModelFactory: PaymentMethodsViewModel.Factory

    private val viewModel: PaymentMethodsViewModel by fragmentViewModel()
    private lateinit var binding: FragmentPaymentMethodsBinding
    private lateinit var controller: PaymentMethodsEpoxyController
    private var currentSnackBar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPaymentMethodsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = PaymentMethodsEpoxyController(object : PaymentMethodsEpoxyController.Callbacks {
            override fun onAddBtnClicked() {
                clearSnackBar()
                viewModel.onAddBtnClicked()
            }

            override fun onDeleteBtnClicked(paymentMethod: PaymentMethod) {
                clearSnackBar()
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

        viewModel.error.observe(viewLifecycleOwner, Observer<Throwable> { throwable ->
            this.view?.let { view ->
                throwable?.let {
                    SnackBarExtensions {
                        currentSnackBar = it.getErrorSnackBar(view)
                        currentSnackBar?.show()
                    }
                    viewModel.clearError()
                }
            }
        })
        binding.paymentMethodsRv.setController(controller)
    }

    private fun clearSnackBar() {
        currentSnackBar?.dismiss()
    }

    override fun invalidate() {
        withState(viewModel) {
            binding.state = it
            controller.setData(it)
        }
    }
}