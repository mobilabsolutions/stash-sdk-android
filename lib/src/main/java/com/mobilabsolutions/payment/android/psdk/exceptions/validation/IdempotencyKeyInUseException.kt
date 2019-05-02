package com.mobilabsolutions.payment.android.psdk.exceptions.validation

import com.mobilabsolutions.payment.android.psdk.exceptions.base.ValidationException

class IdempotencyKeyInUseException : ValidationException(MESSAGE) {
    companion object {
        @JvmStatic
        val MESSAGE = "Idempotency key in use for different payment method type"
    }
}