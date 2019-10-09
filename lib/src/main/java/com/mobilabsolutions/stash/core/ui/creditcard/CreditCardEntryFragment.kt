package com.mobilabsolutions.stash.core.ui.creditcard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.stash.core.databinding.FragmentCreditCardEntryBinding
import com.mobilabsolutions.stash.core.internal.StashImpl
import com.mobilabsolutions.stash.core.internal.uicomponents.CreditCardDataValidator
import com.mobilabsolutions.stash.core.internal.uicomponents.PersonalDataValidator
import com.mobilabsolutions.stash.core.internal.uicomponents.SnackBarExtensions.getErrorSnackBar
import com.mobilabsolutions.stash.core.internal.uicomponents.getCardNumberStringUnformatted
import com.mobilabsolutions.stash.core.ui.textfield.TextFieldFragment
import org.threeten.bp.LocalDate
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
class CreditCardEntryFragment : BaseMvRxFragment() {
    @Inject
    lateinit var viewModelFactory: CreditCardEntryViewModel.Factory
    @Inject
    lateinit var creditCardDataValidator: CreditCardDataValidator
    @Inject
    lateinit var personalDataValidator: PersonalDataValidator

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
        binding.pager.apply {
            pageMargin = 16
            offscreenPageLimit = 4
            adapter = CreditCardEntryPagerAdapter(childFragmentManager)
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                    // do nothing
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    // do nothing
                }

                override fun onPageSelected(position: Int) {
                    viewModel.onPositionChanged(position)
                }
            })
        }
        with(binding) {
            backArrow.setOnClickListener { requireActivity().onBackPressed() }
            btnNext.setOnClickListener {
                pager.currentItem = pager.currentItem + 1
            }
            btnBack.setOnClickListener {
                pager.currentItem = pager.currentItem - 1
            }
            btnSave.setOnClickListener {
                withState(viewModel) { state ->
                    var validationResult = creditCardDataValidator.validateCreditCardNumber(state.cardNumber.getCardNumberStringUnformatted().trim())
                    if (!validationResult.success) {
                        showError(validationResult.errorMessageResourceId)
                        binding.pager.currentItem = 0
                        return@withState
                    }
                    validationResult = personalDataValidator.validateName(state.name)
                    if (!validationResult.success) {
                        showError(validationResult.errorMessageResourceId)
                        binding.pager.currentItem = 1
                        return@withState
                    }
                    validationResult = creditCardDataValidator.validateExpiry(expiryDate = LocalDate.now())
                    if (!validationResult.success) {
                        showError(validationResult.errorMessageResourceId)
                        binding.pager.currentItem = 2
                        return@withState
                    }
                    validationResult = creditCardDataValidator.validateCvv(state.cvv)
                    if (!validationResult.success) {
                        showError(validationResult.errorMessageResourceId)
                        binding.pager.currentItem = 3
                        return@withState
                    }
                    viewModel.onSaveBtnClicked()
                }

            }
            cardNumberText.setOnClickListener { pager.currentItem = 0 }
            nameLayout.setOnClickListener { pager.currentItem = 1 }
            extLayout.setOnClickListener { pager.currentItem = 2 }
        }
    }

    private fun showError(errorMessageResourceId: Int) {
        getErrorSnackBar(binding.root, errorMessageResourceId).show()
    }

    override fun invalidate() {
        withState(viewModel) { state ->
            binding.state = state
            binding.btnBack.isVisible = state.currentPosition != 0
            binding.btnSave.isVisible = state.currentPosition == 4
            binding.btnNext.visibility = if (state.currentPosition == 4) View.INVISIBLE else View.VISIBLE
            state.cardIconResId.let { resId ->
                if (resId != -1) {
                    binding.cardIc.setImageResource(resId)
                }
            }
            binding.btnNext.isEnabled = when (state.currentPosition) {
                0 -> state.cardNumber.isNotEmpty()
                1 -> state.name.isNotEmpty()
                2 -> state.expDate.isNotEmpty()
                3 -> state.cvv.isNotEmpty()
                else -> true
            }
            if (state.currentPosition == 3) {
                if (binding.flipView.isFrontSide) {
                    binding.flipView.flipTheView()
                }
            } else {
                if (binding.flipView.isBackSide) {
                    binding.flipView.flipTheView()
                }
            }
            state.validationResult?.let {
                if (!it.success) {
                    Toast.makeText(requireContext(), it.errorMessageResourceId, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    class CreditCardEntryPagerAdapter(
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
