package com.mobilabsolutions.stash.core.ui.creditcard

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 24-09-2019.
 */
@Singleton
class CreditCardEntryController @Inject constructor(

) {
    private val _creditCardNumberChannel = ConflatedBroadcastChannel<String>()
    private val _iconResIdChannel = ConflatedBroadcastChannel<Int>()
    private val _nameChannel = ConflatedBroadcastChannel<String>()
    private val _expDateChannel = ConflatedBroadcastChannel<String>()
    private val _cvvChannel = ConflatedBroadcastChannel<String>()

    fun observeCardNumberText(): Flow<String> = _creditCardNumberChannel.asFlow()
    fun observeIconResId(): Flow<Int> = _iconResIdChannel.asFlow()
    fun observeNameText(): Flow<String> = _nameChannel.asFlow()
    fun observeExpDateText(): Flow<String> = _expDateChannel.asFlow()
    fun observeCvv(): Flow<String> = _cvvChannel.asFlow()

    fun onCreditCardNumberChanged(cardNumberText: String) {
        _creditCardNumberChannel.offer(cardNumberText)
    }

    fun onIconResIdChanged(iconResId: Int) {
        _iconResIdChannel.offer(iconResId)
    }

    fun onNameTextChanged(text: String) {
        _nameChannel.offer(text)
    }

    fun onExpDateTextChanged(text: String) {
        _expDateChannel.offer(text)
    }

    fun onCvvTextChanged(text: String) {
        _cvvChannel.offer(text)
    }

    fun clearAll() {
        _creditCardNumberChannel.offer("")
        _iconResIdChannel.offer(-1)
        _nameChannel.offer("")
        _expDateChannel.offer("")
        _cvvChannel.offer("")
    }
}