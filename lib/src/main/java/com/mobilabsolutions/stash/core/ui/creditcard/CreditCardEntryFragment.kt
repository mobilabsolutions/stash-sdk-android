package com.mobilabsolutions.stash.core.ui.creditcard

import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.mobilabsolutions.stash.core.util.BaseFragment
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
class CreditCardEntryFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: CreditCardEntryViewModel.Factory

    private val viewModel: CreditCardEntryViewModel by fragmentViewModel()

    override fun invalidate() {
        withState(viewModel) {

        }
    }
}