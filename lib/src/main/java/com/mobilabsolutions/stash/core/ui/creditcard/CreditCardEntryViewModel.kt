package com.mobilabsolutions.stash.core.ui.creditcard

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.core.internal.uicomponents.UiRequestHandler
import com.mobilabsolutions.stash.core.util.BaseViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.wajahatkarim3.easyflipview.EasyFlipView
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 17-09-2019.
 */
class CreditCardEntryViewModel @AssistedInject constructor(
    @Assisted initialState: CreditCardEntryViewState,
    private val uiRequestHandler: UiRequestHandler,
    private val creditCardEntryController: CreditCardEntryController
) : BaseViewModel<CreditCardEntryViewState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: CreditCardEntryViewState): CreditCardEntryViewModel
    }

    companion object : MvRxViewModelFactory<CreditCardEntryViewModel, CreditCardEntryViewState> {
        override fun create(viewModelContext: ViewModelContext, state: CreditCardEntryViewState): CreditCardEntryViewModel? {
            val fragment: CreditCardEntryFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.viewModelFactory.create(state)
        }
    }

    enum class CreditCardTextField {
        CARD_NUMBER,
        NAME,
        EXP_DATE,
        CVV,
        COUNTRY
    }

    private val _flipState = ConflatedBroadcastChannel<EasyFlipView.FlipState>()

    init {
        viewModelScope.launch {
            _flipState.asFlow()
                .distinctUntilChanged()
                .execute {
                    val update = it() ?: EasyFlipView.FlipState.FRONT_SIDE
                    copy(shouldFlip = update != currentCardSide)
                }
        }

        viewModelScope.launch {
            creditCardEntryController.observeCardNumberText()
                .execute {
                    copy(cardNumber = it().orEmpty())
                }
        }
        viewModelScope.launch {
            creditCardEntryController.observeIconResId()
                .execute {
                    copy(cardIconResId = it())
                }
        }
        viewModelScope.launch {
            creditCardEntryController.observeNameText()
                .execute {
                    copy(name = it().orEmpty())
                }
        }
        viewModelScope.launch {
            creditCardEntryController.observeExpDateText()
                .execute {
                    copy(expDate = it().orEmpty())
                }
        }

        viewModelScope.launch {
            creditCardEntryController.observeCvv()
                .execute {
                    copy(cvv = it().orEmpty())
                }
        }
    }

    fun onPositionChanged(position: Int) {
        setState {
            copy(currentPosition = position)
        }
    }

    fun onCardFlipped(newCurrentSide: EasyFlipView.FlipState) {
        _flipState.offer(newCurrentSide)
    }

    fun onSaveBtnClicked() {
    }
}