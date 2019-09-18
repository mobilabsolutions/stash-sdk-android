package com.mobilabsolutions.stash.core.ui.creditcard

import com.airbnb.epoxy.TypedEpoxyController
import com.mobilabsolutions.stash.core.creditCardTextField

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 18-09-2019.
 */
class CreditCardEntryController : TypedEpoxyController<CreditCardEntryViewState>() {
    override fun buildModels(data: CreditCardEntryViewState) {
        data.fields.forEach {
            creditCardTextField {
                id(it.name)
            }
        }
    }
}