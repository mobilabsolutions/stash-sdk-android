package com.mobilabsolutions.stash.core.ui.creditcard

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import androidx.fragment.app.Fragment
import com.mobilabsolutions.stash.core.databinding.ViewHolderCreditCardTextFieldBinding
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryViewModel.CreditCardTextField.CARD_NUMBER
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryViewModel.CreditCardTextField.COUNTRY
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryViewModel.CreditCardTextField.CVV
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryViewModel.CreditCardTextField.EXP_DATE
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryViewModel.CreditCardTextField.NAME
import com.mobilabsolutions.stash.core.ui.creditcard.CreditCardEntryViewModel.CreditCardTextField.values

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-09-2019.
 */
class TextFieldFragment : Fragment() {
    companion object {
        @JvmStatic
        fun create(position: Int): TextFieldFragment {
            return TextFieldFragment().apply {
                arguments = Bundle().also {
                    it.putInt("key", position)
                }
            }
        }
    }

    private lateinit var binding: ViewHolderCreditCardTextFieldBinding
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

        val position = arguments?.getInt("key") ?: 0
        val field = values()[position]
        binding.field = field

        val inputType: Int = when (field) {
            CARD_NUMBER -> InputType.TYPE_CLASS_NUMBER
            NAME -> InputType.TYPE_CLASS_TEXT
            EXP_DATE -> InputType.TYPE_CLASS_NUMBER
            CVV -> InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_VARIATION_PASSWORD
            COUNTRY -> InputType.TYPE_CLASS_TEXT
        }
        binding.inputField.inputType = inputType
    }

    override fun onResume() {
        super.onResume()
        binding.inputField.postDelayed({
            binding.inputField.requestFocus()
            val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.inputField, SHOW_IMPLICIT)
        }, 500L)
    }
}