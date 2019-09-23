package com.mobilabsolutions.stash.core.ui.creditcard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
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
        binding.backButton.setOnClickListener { requireActivity().onBackPressed() }
        binding.pager.apply {
            pageMargin = 16
            offscreenPageLimit = 2
        }
        binding.pager.adapter = PagerTestAdapter(childFragmentManager)
        binding.pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                viewModel.onPositionChanged(position)
            }
        })
        binding.btnNext.setOnClickListener {
            binding.pager.currentItem = binding.pager.currentItem + 1
        }
    }

    override fun invalidate() {
        withState(viewModel) {
            binding.state = it
        }
    }

    class PagerTestAdapter(
        fragmentManager: FragmentManager
    ) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val fields = CreditCardEntryViewModel.CreditCardTextField.values()

        override fun getItem(position: Int): Fragment {
            return TextFieldFragment.create(position)
        }

        override fun getCount(): Int {
            return fields.count()
        }
    }
}