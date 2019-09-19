package com.mobilabsolutions.stash.core.ui.creditcard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.stash.core.R
import com.mobilabsolutions.stash.core.databinding.FragmentCreditCardEntryBinding
import com.mobilabsolutions.stash.core.databinding.ViewHolderCreditCardTextFieldBinding
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
        with(binding.textFieldsRv) {
            adapter = controller.adapter
            val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMarginAndOffset)
            val offsetPx = resources.getDimensionPixelOffset(R.dimen.pageMarginAndOffset)
            setPageTransformer { page, position ->
                val viewPager = page.parent.parent as ViewPager2
                val offset = position * -(2 * offsetPx + pageMarginPx)
                if (viewPager.orientation == ORIENTATION_HORIZONTAL) {
                    if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                        page.translationX = -offset
                    } else {
                        page.translationX = offset
                    }
                } else {
                    page.translationY = offset
                }
            }
        }
    }

    override fun invalidate() {
        withState(viewModel) {
            binding.state = it
            controller.setData(it)
            val pager = binding.pager
            if (pager.adapter == null) {
                pager.adapter = CardPagerAdapter(it.fields)
                pager.offscreenPageLimit = it.fields.count() - 1

                val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMarginAndOffset)
                val offsetPx = resources.getDimensionPixelOffset(R.dimen.pageMarginAndOffset)
                pager.setPageTransformer { page, position ->
                    val viewPager = page.parent.parent as ViewPager2
                    val offset = position * -(2 * offsetPx + pageMarginPx)
                    if (viewPager.orientation == ORIENTATION_HORIZONTAL) {
                        if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                            page.translationX = -offset
                        } else {
                            page.translationX = offset
                        }
                    } else {
                        page.translationY = offset
                    }
                }
            }
        }
    }

    class CardPagerAdapter(
        private val fields: List<CreditCardEntryViewModel.CreditCardTextField>
    ) : RecyclerView.Adapter<CardViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            return CardViewHolder.from(parent)
        }

        override fun getItemCount(): Int {
            return fields.count()
        }

        override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
            holder.bind(field = fields[position])
        }
    }

    class CardViewHolder(
        private val binding: ViewHolderCreditCardTextFieldBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): CardViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ViewHolderCreditCardTextFieldBinding.inflate(layoutInflater, parent, false)
                return CardViewHolder(binding)
            }
        }

        fun bind(field: CreditCardEntryViewModel.CreditCardTextField) {
            binding.field = field
            binding.executePendingBindings()
        }
    }
}