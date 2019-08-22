/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.payments.selectpayment

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.stash.core.internal.uicomponents.SnackBarExtensions
import com.mobilabsolutions.stash.sample.core.BaseFragment
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.databinding.FragmentSelectPaymentBinding
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

class SelectPaymentFragment : BaseFragment() {
    companion object {
        @JvmStatic
        fun create(payAmount: Int): SelectPaymentFragment {
            return SelectPaymentFragment().apply {
                arguments = bundleOf(MvRx.KEY_ARG to Arguments(payAmount))
            }
        }
    }

    @Parcelize
    data class Arguments(val payAmount: Int) : Parcelable

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
                viewModel.onPaymentMethodSelected(paymentMethod)
                binding.btnPay.isEnabled = true
            }
        })
        binding.paymentMethodsRv.setController(controller)
        binding.back.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.btnPay.setOnClickListener {
            viewModel.onPayClicked()
            activity?.apply {
                setResult(AppCompatActivity.RESULT_OK)
                finish()
            }
        }

        viewModel.error.observe(viewLifecycleOwner, Observer<Throwable> { throwable ->
            this.view?.let {
                SnackBarExtensions {
                    throwable.getErrorSnackBar(it).show()
                }
            }
        })
    }

    override fun invalidate() {
        withState(viewModel) {
            binding.state = it
            controller.setData(it)
        }
    }
}