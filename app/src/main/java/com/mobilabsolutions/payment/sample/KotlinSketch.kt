package com.mobilabsolutions.payment.sample

import android.annotation.SuppressLint
import android.app.Activity
import com.mobilabsolutions.payment.android.psdk.ExtraAliasInfo
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.rxkotlin.subscribeBy
import java.util.UUID

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class KotlinSketch {
    val registrationManager: RegistrationManager = TODO()
    val activity: Activity = TODO()

    val bla = "123"

    fun usingUi() {
        val idempotencyKey = UUID.randomUUID()
        registrationManager.registerPaymentMethodUsingUi(activity, PaymentMethodType.CC, idempotencyKey)
    }

    fun showCreditCardMask(mask: String) {
    }

    fun showSepaMask(mask: String) {
    }

    fun showPayPalEmail(email: String) {
    }

    fun sendAliasToBackend(alias: String) {
    }

    fun handleException(exception: Throwable) {
    }

    fun handleUnknownException(exception: Throwable) {
    }

    @SuppressLint("CheckResult")
    fun readmeSketch() {
        val billingData = BillingData(
            firstName = "Max",
            lastName = "Mustermann",
            city = "Cologne"
        )

        val creditCardData = CreditCardData(
            number = "4111111111111111",
            expiryMonth = 10,
            expiryYear = 2021,
            cvv = "123",
            billingData = billingData
        )

        val sepaData = SepaData(
            iban = "DE123412341234",
            billingData = billingData
        )

        val registrationManager = PaymentSdk.getRegistrationManager()
        registrationManager.registerCreditCard(creditCardData)
            .subscribeBy(
                onSuccess = { paymentAlias ->
                    // Send alias to your backend server for later usage
                    sendAliasToBackend(paymentAlias.alias)
                    // Handle showing credit card payment method in UI, i.e.:
                    val extraAliasInfo = paymentAlias.extraAliasInfo as ExtraAliasInfo.CreditCardExtraInfo
                    showCreditCardMask(extraAliasInfo.creditCardMask)
                },
                onError = {
                    // Handle exceptions
                    handleException(it)
                }

            )

        registrationManager.registerSepaAccount(sepaData)
            .subscribeBy(
                onSuccess = { paymentAlias ->
                    // Send alias to your backend server for later usage
                    sendAliasToBackend(paymentAlias.alias)
                    // Handle showing credit card payment method in UI, i.e.:
                    val extraAliasInfo = paymentAlias.extraAliasInfo as ExtraAliasInfo.SepaExtraInfo
                    showCreditCardMask(extraAliasInfo.maskedIban)
                },
                onError = {
                    // Handle exceptions
                    handleException(it)
                }

            )

        registrationManager.registerPaymentMethodUsingUi()
            .subscribeBy(
                onSuccess = { paymentAlias ->
                    // Send alias to your backend server for later usage
                    sendAliasToBackend(paymentAlias.alias)
                    when (val aliasInfo = paymentAlias.extraAliasInfo) {
                        is ExtraAliasInfo.CreditCardExtraInfo -> {
                            // Handle showing credit card payment method in UI, i.e.:
                            showCreditCardMask(aliasInfo.creditCardMask)
                        }
                        is ExtraAliasInfo.SepaExtraInfo -> {
                            // Handle showing SEPA payment method in UI i.e.:
                            showSepaMask(aliasInfo.maskedIban)
                        }
                        is ExtraAliasInfo.PaypalExtraInfo -> {
                            // Handle showing PayPal payment method in UI i.e.:
                            showPayPalEmail(aliasInfo.email)
                        }
                    }
                },
                onError = {
                    // Handle exceptions
                    handleException(it)
                }

            )
    }
}