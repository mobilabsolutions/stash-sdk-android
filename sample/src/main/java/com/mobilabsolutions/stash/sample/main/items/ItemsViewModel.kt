/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.main.items

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.sample.core.BaseViewModel
import com.mobilabsolutions.stash.sample.core.launchInteractor
import com.mobilabsolutions.stash.sample.data.entities.Product
import com.mobilabsolutions.stash.sample.data.interactors.AddCart
import com.mobilabsolutions.stash.sample.data.interactors.LoadProducts
import com.mobilabsolutions.stash.sample.util.AppRxSchedulers
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class ItemsViewModel @AssistedInject constructor(
    @Assisted initialState: ItemsViewState,
    schedulers: AppRxSchedulers,
    loadProducts: LoadProducts,
    private val addCart: AddCart
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
                    copy(products = it)
                }

        loadProducts.setParams(Unit)
    }

    fun onClick(product: Product) {
        scope.launchInteractor(addCart, AddCart.ExecuteParams(product))
    }
}