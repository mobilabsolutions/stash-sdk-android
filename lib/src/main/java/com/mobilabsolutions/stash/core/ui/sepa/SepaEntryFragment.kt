package com.mobilabsolutions.stash.core.ui.sepa

import com.mobilabsolutions.stash.core.util.BaseFragment
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
class SepaEntryFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: SepaEntryViewModel.Factory

    override fun invalidate() {
    }
}