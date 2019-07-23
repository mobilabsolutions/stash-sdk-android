/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.main.checkout

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.sample.core.BaseViewModel
import com.mobilabsolutions.stash.sample.core.launchInteractor
import com.mobilabsolutions.stash.sample.data.interactors.ChangeCartQuantity
import com.mobilabsolutions.stash.sample.data.interactors.LoadCart
import com.mobilabsolutions.stash.sample.data.resultentities.CartWithProduct
import com.mobilabsolutions.stash.sample.util.AppRxSchedulers
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class CheckoutViewModel @AssistedInject constructor(
    @Assisted initialState: CheckoutViewState,
    schedulers: AppRxSchedulers,
    loadCart: LoadCart,
    private val changeCartQuantity: ChangeCartQuantity
) : BaseViewModel<CheckoutViewState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: CheckoutViewState): CheckoutViewModel
    }

    companion object : MvRxViewModelFactory<CheckoutViewModel, CheckoutViewState> {
        override fun create(viewModelContext: ViewModelContext, state: CheckoutViewState): CheckoutViewModel? {
            val fragment: CheckoutFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.checkoutViewModelFactory.create(state)
        }
    }

    init {
        loadCart.observe()
                .subscribeOn(schedulers.io)
                .execute {
                    val carts = it() ?: emptyList()
                    var totalPrice = 0
                    carts.forEach {
                        it.entry?.let { cart ->
                            totalPrice += cart.quantity * it.product.price
                        }
                    }
                    copy(cartItems = it() ?: emptyList(), totalAmount = totalPrice, showEmptyView = carts.isEmpty())
                }
        loadCart.setParams(Unit)
    }

    fun onAddButtonClicked(cartWithProduct: CartWithProduct) {
        scope.launchInteractor(changeCartQuantity, ChangeCartQuantity.ExecuteParams(true, cartWithProduct))
    }

    fun onRemoveButtonClicked(cartWithProduct: CartWithProduct) {
        scope.launchInteractor(changeCartQuantity, ChangeCartQuantity.ExecuteParams(false, cartWithProduct))
    }
}