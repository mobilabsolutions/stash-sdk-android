package com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration

import com.mobilabsolutions.payment.android.psdk.internal.api.backend.MobilabApi
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.UpdatePaymentAliasRequest
import com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.pspapi.BsPayonePaymentRequest
import com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.pspapi.OldBsPayoneApi
import com.mobilabsolutions.payment.android.util.BSUtils
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class OldBsPayoneHandler @Inject constructor(private val oldBsPayoneApi: OldBsPayoneApi, private val mobilabApi: MobilabApi) {

    fun registerCreditCard(requestedAlias: String, bsOldRegistrationRequest: BsOldRegistrationRequest): Single<String> {

        return oldBsPayoneApi.registerCreditCard(
                authorization = getAuthorizationString(bsOldRegistrationRequest),
                bsPayonePaymentRequest = BsPayonePaymentRequest
                        .fromBsOldPaymentRequest(
                                bsOldRegistrationRequest
                        )
        ).map {
            val responseCode = it.apiResponse?.response?.rc ?: -1
            when (responseCode) {
                -1 -> throw RuntimeException("Missing response code") // TODO specify error
                0 -> {
                    it.apiResponse?.aliasResponse?.alias
                }
                1343 -> {
                    mobilabApi.updatePaymentMethodAlias(
                            UpdatePaymentAliasRequest(
                                    it.apiResponse?.response?.creditCard?.panAlias.toString(),
                                    requestedAlias
                            )
                    ).blockingAwait()
                    requestedAlias
                }
                else -> throw RuntimeException("Unexpected response from BS Api") // TODO specify error
            }
        }
    }

    private fun getAuthorizationString(pbsOldRegistrationRequest: BsOldRegistrationRequest): String {
        return "Basic ${BSUtils.getBasicAuthString(pbsOldRegistrationRequest.username, pbsOldRegistrationRequest.password)}"
    }
}
