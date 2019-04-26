package com.mobilabsolutions.payment.sample.registration

import com.mobilabsolutions.commonsv3.mvp.presenter.CommonPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class RegistrationPresenter : CommonPresenter<RegistrationView>() {

    var registrationViewState: RegistrationViewState = RegistrationViewState()

    @Inject
    lateinit var registrationController: RegistrationController

    fun registerSepaRequested() {
        registrationViewState = registrationViewState.copy(enteringData = false, executingRegistration = true, registrationFailed = false)
        applyOnUi {
            it.renderState(registrationViewState)

            registrationViewState.apply {
                registrationController.registerSepa(
                        holderName = holderName,
                        iban = iban,
                        bic = bic,
                        activity = it.getParentActivity()
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onSuccess = {
                                    registrationViewState = copy(executingRegistration = false, successfullRegistration = Pair(true, it.alias))
                                    applyOnUi { it.renderState(registrationViewState) }
                                },
                                onError = {
                                    Timber.d(it, "Error encountered")
                                    registrationViewState = copy(
                                            executingRegistration = false,
                                            registrationFailed = true,
                                            failureReason = it.message ?: "Unknown error")
                                    applyOnUi { it.renderState(registrationViewState) }
                                }
                        )
            }
        }
    }

    fun registerCreditCardRequested() {
        registrationViewState = registrationViewState.copy(enteringData = false, executingRegistration = true, registrationFailed = false)
        applyOnUi {
            it.renderState(registrationViewState)

            registrationViewState.apply {
                registrationController.registerCreditCard(
                        holderName = holderName,
                        address = address,
                        city = city,
                        country = country,
                        phone = phone,
                        creditCardNumber = creditCardNumber,
                        cvv = cvv,
                        exipryDate = expiryDate,
                        activity = it.getParentActivity()
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onSuccess = {
                                    registrationViewState = copy(executingRegistration = false, successfullRegistration = Pair(true, it.alias))
                                    applyOnUi { it.renderState(registrationViewState) }
                                },
                                onError = {
                                    Timber.d(it, "Error encountered")
                                    registrationViewState = copy(
                                            executingRegistration = false,
                                            registrationFailed = true,
                                            failureReason = it.message ?: "Unknown error")
                                    applyOnUi { it.renderState(registrationViewState) }
                                }
                        )
            }
        }
    }

    fun registerPayPalRequested() {
        applyOnUi {
            registrationViewState = registrationViewState.copy(enteringData = false, executingRegistration = true, registrationFailed = false)
            registrationViewState.apply {
                registrationController.registerPayPal(it.getParentActivity())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onSuccess = {
                                    registrationViewState = copy(executingRegistration = false, successfullRegistration = Pair(true, it.alias))
                                    applyOnUi { it.renderState(registrationViewState) }
                                },
                                onError = {
                                    Timber.d(it, "Error encountered")
                                    registrationViewState = copy(
                                            executingRegistration = false,
                                            registrationFailed = true,
                                            failureReason = it.message ?: "Unknown error")
                                    applyOnUi { it.renderState(registrationViewState) }
                                }

                        )
            }
        }
    }

    fun registerPaymentMethodRequested() {
        registrationViewState = registrationViewState.copy(enteringData = false, executingRegistration = true, registrationFailed = false)
        applyOnUi {
            it.renderState(registrationViewState)

            registrationViewState.apply {
                registrationController.registerPaymentMethod(
                        activity = it.getParentActivity()
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onSuccess = {
                                    registrationViewState = copy(executingRegistration = false, successfullRegistration = Pair(true, it.alias))
                                    applyOnUi { it.renderState(registrationViewState) }
                                },
                                onError = {
                                    Timber.d(it, "Error encountered")
                                    registrationViewState = copy(
                                            executingRegistration = false,
                                            registrationFailed = true,
                                            failureReason = it.message ?: "Unknown error")
                                    applyOnUi { it.renderState(registrationViewState) }
                                }
                        )
            }
        }
    }

    override fun onUiVisible() {
        super.onUiVisible()
        Timber.d("Ui visible")
        applyOnUi { ui ->

            ui.holderNameObservable.subscribeBy(onNext = { registrationViewState = registrationViewState.copy(holderName = it, enteringData = true) })
            ui.cityObservable.subscribeBy(onNext = { registrationViewState = registrationViewState.copy(city = it, enteringData = true) })
            ui.phoneObservable.subscribeBy(onNext = { registrationViewState = registrationViewState.copy(phone = it, enteringData = true) })
            ui.addressObservable.subscribeBy(onNext = { registrationViewState = registrationViewState.copy(address = it, enteringData = true) })
            ui.countryObservable.subscribeBy(onNext = { registrationViewState = registrationViewState.copy(country = it, enteringData = true) })
            ui.creditCardNumberObservable.subscribeBy(onNext = { registrationViewState = registrationViewState.copy(creditCardNumber = it, enteringData = true) })
            ui.cvvObservable.subscribeBy(onNext = { registrationViewState = registrationViewState.copy(cvv = it, enteringData = true) })
            ui.expiryDateObservable.subscribeBy(onNext = { registrationViewState = registrationViewState.copy(expiryDate = it, enteringData = true) })
            ui.ibanObservable.subscribeBy(onNext = { registrationViewState = registrationViewState.copy(iban = it, enteringData = true) })
            ui.bicObservable.subscribeBy(onNext = { registrationViewState = registrationViewState.copy(bic = it, enteringData = true) })
        }
    }
}