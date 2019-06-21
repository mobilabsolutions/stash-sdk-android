package com.mobilabsolutions.payment.android.psdk.internal.uicomponents

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView

/**
 * @author [Ugljesa Jovanovic](ugi@mobilabsolutions.com)
 */

fun EditText.observeText(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(
        object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onTextChanged.invoke(p0.toString())
            }
        }
    )
}

fun EditText.getContentsAsString(): String = this.text.toString()

fun TextView.getContentsAsString(): String = this.text.toString()

fun String.getCardNumberStringUnformatted(): String = this.replace("\\D".toRegex(), "")

fun String.getIbanStringUnformatted(): String = this.replace("\\s".toRegex(), "")

fun EditText.focusObserver(onFocusChanged: (Boolean) -> Unit) {
    this.onFocusChangeListener = View.OnFocusChangeListener { _, p1 -> onFocusChanged.invoke(p1) }
}

fun TextView.focusObserver(onFocusChanged: (Boolean) -> Unit) {
    this.onFocusChangeListener = View.OnFocusChangeListener { _, p1 -> onFocusChanged.invoke(p1) }
}

fun EditText.getContentOnFocusLost(contentOnFocusLost: (String) -> Unit) {
    this.focusObserver { if (!it) contentOnFocusLost.invoke(this.getContentsAsString()) }
}

fun TextView.onFocusGained(contentOnFocusGained: () -> Unit) {
    this.focusObserver { if (it) contentOnFocusGained.invoke() }
}
