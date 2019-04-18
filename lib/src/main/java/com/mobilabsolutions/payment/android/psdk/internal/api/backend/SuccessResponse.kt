package com.mobilabsolutions.payment.android.psdk.internal.api.backend

/**
 * Represents a successful response body.
 *
 * @param <T>
 * the type of the response
 * @author Radu Creanga <radu></radu>@mobilabsolutions.com>
</T> */
class SuccessResponse<T>(val result: T) {

    override fun toString(): String {
        return String.format("SuccessResponse{result=%s}", this.result)
    }
}
