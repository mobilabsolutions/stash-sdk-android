package com.mobilabsolutions.stash.core.ui.creditcard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.stash.core.databinding.FragmentCreditCardEntryBinding
import com.mobilabsolutions.stash.core.internal.StashImpl
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
class CreditCardEntryFragment : BaseMvRxFragment() {
    @Inject
    lateinit var viewModelFactory: CreditCardEntryViewModel.Factory

    private val viewModel: CreditCardEntryViewModel by fragmentViewModel()
    private lateinit var binding: FragmentCreditCardEntryBinding
    private lateinit var controller: CreditCardEntryController

    override fun onAttach(context: Context) {
        StashImpl.getInjector().inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCreditCardEntryBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = CreditCardEntryController()
        binding.textFieldsRv.setController(controller)
    }

    override fun invalidate() {
        withState(viewModel) {
            binding.state = it
            controller.setData(it)
        }
    }
}