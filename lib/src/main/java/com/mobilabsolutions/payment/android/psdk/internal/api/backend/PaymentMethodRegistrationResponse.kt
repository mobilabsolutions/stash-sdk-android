package com.mobilabsolutions.payment.android.psdk.internal.api.backend

import com.google.gson.annotations.SerializedName

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PaymentMethodRegistrationResponse(
    var paymentAlias: String,
    var url: String? = null,
    var merchantId: String,
    var action: String? = null,
    var panAlias: String,
    var username: String? = null,
    var password: String? = null,
    var eventExtId: String? = null,
    var amount: String? = null,
    var currency: String? = null,
    var kind: String? = null,
    @SerializedName("extra")
    val providerSpecificData: Map<String, String> = emptyMap()

)