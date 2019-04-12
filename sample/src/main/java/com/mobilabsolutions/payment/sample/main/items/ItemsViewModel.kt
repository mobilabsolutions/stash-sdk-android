package com.mobilabsolutions.payment.sample.main.items

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.payment.sample.core.BaseViewModel
import com.mobilabsolutions.payment.sample.data.interactors.LoadProducts
import com.mobilabsolutions.payment.sample.util.AppRxSchedulers
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class ItemsViewModel @AssistedInject constructor(
        @Assisted initialState: ItemsViewState,
        schedulers: AppRxSchedulers,
        private val loadProducts: LoadProducts
) : BaseViewModel<ItemsViewState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: ItemsViewState): ItemsViewModel
    }

    companion object : MvRxViewModelFactory<ItemsViewModel, ItemsViewState> {
        override fun create(viewModelContext: ViewModelContext, state: ItemsViewState): ItemsViewModel? {
            val fragment: ItemsFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.itemsViewModelFactory.create(state)
        }
    }

    init {
        loadProducts.observe()
                .subscribeOn(schedulers.io)
                .execute {
                    copy(products = it()?: emptyList())
                }

        loadProducts.setParams(Unit)
    }
}