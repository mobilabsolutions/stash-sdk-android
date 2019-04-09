package com.mobilabsolutions.payment.sample.main.items

import com.mobilabsolutions.payment.sample.core.BaseFragment
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class ItemsFragment : BaseFragment() {
    @Inject
    lateinit var itemsViewModelFactory: ItemsViewModel.Factory

    override fun invalidate() {

    }
}