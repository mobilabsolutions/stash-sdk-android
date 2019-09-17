package com.mobilabsolutions.stash.core.ui.picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.PaymentMethodType.CC
import com.mobilabsolutions.stash.core.PaymentMethodType.PAYPAL
import com.mobilabsolutions.stash.core.PaymentMethodType.SEPA
import com.mobilabsolutions.stash.core.R
import com.mobilabsolutions.stash.core.databinding.FragmentPaymentPickerBinding
import com.mobilabsolutions.stash.core.util.BaseFragment
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
class PaymentPickerFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: PaymentPickerViewModel.Factory
    @Inject
    lateinit var controller: PaymentPickerController

    private val viewModel: PaymentPickerViewModel by fragmentViewModel()
    private lateinit var binding: FragmentPaymentPickerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPaymentPickerBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.deep_dark_blue)

        controller.callbacks = object : PaymentPickerController.Callbacks {
            override fun onPaymentMethodClicked(paymentMethodType: PaymentMethodType) {
                when (paymentMethodType) {
                    CC -> findNavController().navigate(R.id.navigation_credit_card_entry)
                    SEPA -> TODO()
                    PAYPAL -> TODO()
                }
            }
        }
        binding.paymentMethodChooserRv.setController(controller)
    }

    override fun invalidate() {
        withState(viewModel) {
            binding.state = it
            controller.setData(it)
        }
    }
}