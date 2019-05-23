package com.mobilabsolutions.payment.android.psdk

import android.app.Activity
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.Single
import org.threeten.bp.LocalDate
import java.util.UUID

/**
 * @author [Ugi](ugi@mobilabsolutions.com)
 */
interface RegistrationManager {

    /**
     * Register a credit card so you can use payment aliasId for future payments
     * @param creditCardData credit card information
     * @return string representing payment aliasId
     */
    fun registerCreditCard(creditCardData: CreditCardData, billingData: BillingData = BillingData(), idempotencyKey: UUID? = null): Single<PaymentMethodAlias>

    /**
     * Register a sepa debit account so you can use payment aliasId for future payments
     * @param sepaData sepa card information
     * @return string representing payment aliasId
     */
    fun registerSepaAccount(sepaData: SepaData, billingData: BillingData = BillingData(), idempotencyKey: UUID? = null): Single<PaymentMethodAlias>

    /**
     * Returns a list of supported payment methods
     * @return list of supported payment methods
     */
    fun getAvailablePaymentMethodsTypes(): Set<PaymentMethodType>

    /**
     * Let payment SDK handle data using built-in UI components
     *
     * @param activity the activity context to launch from. If activity is null, a new task will be created
     * @param specificPaymentMethodType skip payment method chooser and immediately show specific type entry UI
     * @returnstring string representing aliasId
     */
    fun registerPaymentMethodUsingUi(activity: Activity? = null, specificPaymentMethodType: PaymentMethodType? = null, idempotencyKey: UUID? = null): Single<PaymentMethodAlias>
}


data class PaymentMethodAlias(
    val alias: String,
    val paymentMethodType: PaymentMethodType,
    val creditCardExtraInfo: CreditCardExtraInfo? = null,
    val sepaExtraInfo: SepaExtraInfo? = null,
    val paypalExtraInfo: PaypalExtraInfo? = null
)


    data class CreditCardExtraInfo(
        val creditCardMask: String,
        val expiryMonth: Int,
        val expiryYear: Int,
        val creditCardType: CreditCardType
    )

    data class SepaExtraInfo(
        val iban: String
    )

    data class PaypalExtraInfo(
        val email: String
    )



enum class CreditCardType(val regex: Regex) {
    JCB(Regex("^(?:2131|1800|35[0-9]{3})[0-9]{3,}$")),
    AMEX(Regex("^3[47][0-9]{1,13}$")),
    DINERS(Regex("^3(?:0[0-5]|[68][0-9])[0-9]{2,}$")),
    VISA(Regex("^4[0-9]{2,12}(?:[0-9]{3})?$")),
    MAESTRO_13(Regex("^50[0-9]{1,11}$")),
    MAESTRO_15(Regex("^5[68][0-9]{1,13}$")),
    MASTER_CARD(Regex("^5[1-5][0-9]{1,14}$")),
    DISCOVER(Regex("^6(?:011|5[0-9]{2})[0-9]{3,}$")),
    UNIONPAY_16(Regex("^62[0-9]{1,14}$")),
    UNIONPAY_19(Regex("^62[0-9]{15,17}$")),
    MAESTRO(Regex("^6[0-9]{1,18}$")),
    UNKNOWN(Regex(""));


    companion object {
        fun resolveCreditCardType(creditCardNumber: String): CreditCardType {
            return when {
                AMEX.regex.matches(creditCardNumber) -> AMEX
                DINERS.regex.matches(creditCardNumber) -> DINERS
                VISA.regex.matches(creditCardNumber) -> VISA
                MAESTRO_13.regex.matches(creditCardNumber) -> MAESTRO_13
                MAESTRO_15.regex.matches(creditCardNumber) -> MAESTRO_15
                MASTER_CARD.regex.matches(creditCardNumber) -> MASTER_CARD
                DISCOVER.regex.matches(creditCardNumber) -> DISCOVER
                UNIONPAY_16.regex.matches(creditCardNumber) -> UNIONPAY_16
                UNIONPAY_19.regex.matches(creditCardNumber) -> UNIONPAY_19
                MAESTRO.regex.matches(creditCardNumber) -> MAESTRO
                else -> UNKNOWN
            }
        }
    }


}


