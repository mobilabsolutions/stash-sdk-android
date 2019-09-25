package com.mobilabsolutions.stash.core.ui.textfield

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Parcelable
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.InputType.TYPE_CLASS_PHONE
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
import android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
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
import com.mobilabsolutions.stash.core.internal.uicomponents.observeText
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryViewModel
import com.mobilabsolutions.stash.core.util.hideSoftInput
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
        withState(viewModel) {
            val field = it.field
            if (field == CreditCardEntryViewModel.CreditCardTextField.CARD_NUMBER) {
                binding.inputField.addTextChangedListener(CardNumberTextWatcher { resourceId ->
                    viewModel.onCreditCardIconChanged(resourceId)
                })
            }
        }
        binding.inputField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }
        binding.inputField.observeText { viewModel.onTextChanged(it.trim()) }
    }

    override fun onResume() {
        super.onResume()
        binding.inputField.postDelayed({
            binding.inputField.requestFocus()
            val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.inputField, SHOW_IMPLICIT)
        }, 500L)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().hideSoftInput()
    }

    override fun invalidate() {
        withState(viewModel) { state ->
            val field = state.field
            binding.field = field

            val inputType: Int = when (field) {
                CreditCardEntryViewModel.CreditCardTextField.CARD_NUMBER -> TYPE_CLASS_PHONE + TYPE_TEXT_FLAG_NO_SUGGESTIONS
                CreditCardEntryViewModel.CreditCardTextField.NAME -> TYPE_CLASS_TEXT
                CreditCardEntryViewModel.CreditCardTextField.EXP_DATE -> TYPE_CLASS_NUMBER
                CreditCardEntryViewModel.CreditCardTextField.CVV -> TYPE_CLASS_NUMBER + TYPE_NUMBER_VARIATION_PASSWORD
                CreditCardEntryViewModel.CreditCardTextField.COUNTRY -> TYPE_CLASS_TEXT
            }
            binding.inputField.inputType = inputType
        }
    }
}
