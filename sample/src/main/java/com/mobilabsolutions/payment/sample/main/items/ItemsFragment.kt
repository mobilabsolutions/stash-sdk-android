package com.mobilabsolutions.payment.sample.main.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.payment.sample.core.BaseFragment
import com.mobilabsolutions.payment.sample.databinding.FragmentItemsBinding
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class ItemsFragment : BaseFragment() {
    override val mvrxViewId: String = ItemsFragment::class.java.simpleName

    @Inject
    lateinit var itemsViewModelFactory: ItemsViewModel.Factory

    private val viewModel: ItemsViewModel by fragmentViewModel()
    private lateinit var binding: FragmentItemsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentItemsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun invalidate() {
        withState(viewModel) {

        }
    }
}