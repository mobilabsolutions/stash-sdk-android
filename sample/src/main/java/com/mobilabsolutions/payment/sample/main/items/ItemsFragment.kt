package com.mobilabsolutions.payment.sample.main.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.snackbar.Snackbar
import com.mobilabsolutions.payment.sample.R
import com.mobilabsolutions.payment.sample.core.BaseFragment
import com.mobilabsolutions.payment.sample.productItem
import com.mobilabsolutions.payment.sample.util.withModels
import kotlinx.android.synthetic.main.fragment_items.*
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */

class ItemsFragment : BaseFragment() {
    @Inject
    lateinit var itemsViewModelFactory: ItemsViewModel.Factory

    private val viewModel: ItemsViewModel by fragmentViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_items, container, false)
    }

    override fun invalidate() = withState(viewModel) { state ->
        itemsRecyclerView.withModels {
            state.products()?.forEach { product ->
                productItem {
                    id(product.id)
                    product(product)
                    clickListener { _ ->
                        viewModel.onClick(product)
                        view?.let { Snackbar.make(it, getString(R.string.message, product.name), Snackbar.LENGTH_SHORT).show() }
                    }
                }
            }
        }
    }
}