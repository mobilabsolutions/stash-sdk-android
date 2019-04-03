package com.mobilabsolutions.payment.sample.registration

import android.app.Activity
import com.mobilabsolutions.commonsv3.mvp.controller.Controller
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.RegistrationManager
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.CreditCardData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import com.mobilabsolutions.payment.sample.state.PaymentMethodState
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.threeten.bp.LocalDate
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Singleton
class RegistrationController @Inject constructor() : Controller() {


    @Inject
    lateinit var registrationManager: RegistrationManager

    @Inject
    lateinit var paymentMethodStateSubject: BehaviorSubject<PaymentMethodState>

    fun registerCreditCard(
            holderName: String = "",
            address: String = "",
            city: String = "",
            country: String = "",
            phone: String = "",
            creditCardNumber: String,
            cvv: String,
            exipryDate: LocalDate,
            activity: Activity? = null

    ): Single<String> {
        val nameList = holderName.split(' ')
        val firstName = nameList[0]
        val lastName = if (nameList.size > 1) {
            nameList[1]
        } else {
            ""
        }
        return registrationManager.registerPaymentMehodUsingUi(activity, specificPaymentMethodType = PaymentMethodType.CREDITCARD)
//        return registrationManager.registerCreditCard(
//                CreditCardData(number = creditCardNumber,
//                        expiryDate = exipryDate,
//                        cvv = cvv,
//                        holder = holderName
//                )
//          )

                .doOnSuccess {
                    paymentMethodStateSubject.apply {
                        val paymentMethodState = take(1).blockingLast()
                        val paymentMethodMap = paymentMethodState.paymentMethodMap
                        onNext(paymentMethodState.copy(paymentMethodMap = paymentMethodMap + ("CC" + creditCardNumber.takeLast(4) to it)))
                    }
                }
    }

    fun registerSepa(
            holderName: String = "",
            iban: String,
            bic: String,
            address: String = "",
            city: String = "",
            country: String = "",
            phone: String = "",
            activity: Activity? = null
    ): Single<String> {
        val nameList = holderName.split(' ')
        val firstName = nameList[0]
        val lastName = if (nameList.size > 1) {
            nameList[1]
        } else {
            ""
        }
        return registrationManager.registerPaymentMehodUsingUi(activity, specificPaymentMethodType = PaymentMethodType.SEPA)
//        return registrationManager.registerSepa(
//                SepaData(
//                        bic = bic,
//                        iban = iban,
//                        holder = holderName
//                ),
//                BillingData(
//                        firstName, lastName, address, city, country
//                )
//        )
                .doOnSuccess {
                    paymentMethodStateSubject.apply {
                        val paymentMethodState = take(1).blockingLast()
                        val paymentMethodMap = paymentMethodState.paymentMethodMap
                        onNext(paymentMethodState.copy(paymentMethodMap = paymentMethodMap + ("SEPA" + iban.takeLast(4) to it)))
                    }
                }
    }

    fun registerPayPal(activity: Activity? = null): Single<String> {
        return registrationManager.registerPaymentMehodUsingUi(activity = activity, specificPaymentMethodType = PaymentMethodType.PAYPAL)
                .doOnSuccess {
                    Timber.d("Nonce $it")
                    paymentMethodStateSubject.apply {
                        val paymentMethodState = take(1).blockingLast()
                        val paymentMethodMap = paymentMethodState.paymentMethodMap
                        onNext(paymentMethodState.copy(paymentMethodMap = paymentMethodMap + ("PayPal" + LocalDate.now().toString() to it)))
                    }

                }
    }


}