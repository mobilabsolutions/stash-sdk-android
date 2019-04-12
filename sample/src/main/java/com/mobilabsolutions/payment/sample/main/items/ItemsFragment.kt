package com.mobilabsolutions.payment.sample.main.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.withState
import com.mobilabsolutions.payment.sample.R
import com.mobilabsolutions.payment.sample.core.BaseFragment
import com.mobilabsolutions.payment.sample.util.withModels
import kotlinx.android.synthetic.main.fragment_items.*
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */

class ItemsFragment : BaseFragment() {

    @Inject
    lateinit var itemsViewModelFactory: ItemsViewModel.Factory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_items, container, false)
    }

    override fun invalidate() = withState(itemsViewModelFactory.create(ItemsViewState())) { state ->

        itemsRecyclerView.withModels {
            state.items()?.forEach { item ->
                itemRow {
                    item(item)
                }
            }
        }
    }
}