package com.mobilabsolutions.stash.core.ui.textfield

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import androidx.core.os.bundleOf
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.stash.core.databinding.ViewHolderCreditCardTextFieldBinding
import com.mobilabsolutions.stash.core.internal.StashImpl
import com.mobilabsolutions.stash.core.internal.uicomponents.CardNumberTextWatcher
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryViewModel
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-09-2019.
 */
class TextFieldFragment : BaseMvRxFragment() {
    companion object {
        @JvmStatic
        fun create(position: Int): TextFieldFragment {
            return TextFieldFragment().apply {
                arguments = bundleOf(MvRx.KEY_ARG to Arguments(position))
            }
        }
    }

    @Parcelize
    data class Arguments(val position: Int) : Parcelable

    @Inject
    lateinit var viewModelFactory: TextFieldViewModel.Factory

    private val viewModel: TextFieldViewModel by fragmentViewModel()
    private lateinit var binding: ViewHolderCreditCardTextFieldBinding

    override fun onAttach(context: Context) {
        StashImpl.getInjector().inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ViewHolderCreditCardTextFieldBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.inputField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.inputField.postDelayed({
            binding.inputField.requestFocus()
            val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.inputField, SHOW_IMPLICIT)
        }, 500L)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun invalidate() {
        withState(viewModel) { state ->
            val field = state.field
            binding.field = field

            val inputType: Int = when (field) {
                CreditCardEntryViewModel.CreditCardTextField.CARD_NUMBER -> InputType.TYPE_CLASS_NUMBER
                CreditCardEntryViewModel.CreditCardTextField.NAME -> InputType.TYPE_CLASS_TEXT
                CreditCardEntryViewModel.CreditCardTextField.EXP_DATE -> InputType.TYPE_CLASS_NUMBER
                CreditCardEntryViewModel.CreditCardTextField.CVV -> InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_VARIATION_PASSWORD
                CreditCardEntryViewModel.CreditCardTextField.COUNTRY -> InputType.TYPE_CLASS_TEXT
            }
            // binding.inputField.inputType = inputType
            if (field == CreditCardEntryViewModel.CreditCardTextField.CARD_NUMBER) {
                binding.inputField.addTextChangedListener(CardNumberTextWatcher { resourceId ->
                    binding.inputField.setCompoundDrawablesWithIntrinsicBounds(0, 0, resourceId, 0)
                })
            }
        }
    }
}