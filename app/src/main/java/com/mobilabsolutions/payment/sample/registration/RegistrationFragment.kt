package com.mobilabsolutions.payment.sample.registration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobilabsolutions.commonsv3.mvp.view.Ui
import com.mobilabsolutions.commonsv3_dagger.mvp.view.DaggerCommonFragment
import com.mobilabsolutions.payment.sample.R
import com.mobilabsolutions.payment.sample.getBehaviorStringObservable
import com.mobilabsolutions.payment.sample.getStringObservable
import io.reactivex.Observable
import kotlinx.android.synthetic.main.registration_fragment.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import timber.log.Timber

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface RegistrationView : Ui {

    var holderNameObservable: Observable<String>
    var addressObservable: Observable<String>
    var cityObservable: Observable<String>
    var countryObservable: Observable<String>
    var phoneObservable: Observable<String>
    var creditCardNumberObservable: Observable<String>
    var cvvObservable: Observable<String>
    var expiryDateObservable: Observable<LocalDate>

    var ibanObservable : Observable<String>
    var bicObservable : Observable<String>

    var sepaOrCCSelectorObservable : Observable<Boolean>

    fun renderState(registrationViewState: RegistrationViewState)
}


class RegistrationFragment : DaggerCommonFragment<RegistrationPresenter>(), RegistrationView {
    override lateinit var holderNameObservable: Observable<String>
    override lateinit var addressObservable: Observable<String>
    override lateinit var cityObservable: Observable<String>
    override lateinit var countryObservable: Observable<String>
    override lateinit var phoneObservable: Observable<String>
    override lateinit var creditCardNumberObservable: Observable<String>
    override lateinit var cvvObservable: Observable<String>
    override lateinit var expiryDateObservable: Observable<LocalDate>

    override lateinit var ibanObservable: Observable<String>
    override lateinit var bicObservable: Observable<String>
    override lateinit var sepaOrCCSelectorObservable: Observable<Boolean>

    override fun renderState(registrationViewState: RegistrationViewState) {
        registrationStatusTextView.text =
                when {
                    registrationViewState.enteringData -> ""
                    registrationViewState.executingRegistration -> "Executing"
                    registrationViewState.registrationFailed -> "Failed ${registrationViewState.failureReason}"
                    registrationViewState.successfullRegistration.first -> "Success: ${registrationViewState.successfullRegistration.second}"
                    else -> ""
                }

    }

    override val presenterType: Class<RegistrationPresenter> = RegistrationPresenter::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.registration_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerCreditCardButton.setOnClickListener {
            notifyPresenter { it.registerCreditCardRequested() }
        }

        registerSepaButton.setOnClickListener {
            notifyPresenter { it.registerSepaRequested() }
        }

        registerPayPalButton.setOnClickListener {
            notifyPresenter { it.registerPayPalRequested() }
        }

        holderNameObservable = holderNameEditText.getBehaviorStringObservable()
        addressObservable = billingAddressEditText.getBehaviorStringObservable()
        cityObservable = billingCityEditText.getBehaviorStringObservable()
        phoneObservable = billingPhoneEditText.getBehaviorStringObservable()
        countryObservable = billingCountryEditText.getBehaviorStringObservable()
        creditCardNumberObservable = creditCardNumberEditText.getBehaviorStringObservable()
        cvvObservable = cvvEditText.getBehaviorStringObservable()
        ibanObservable = ibanEditText.getBehaviorStringObservable()
        bicObservable = bicEditText.getBehaviorStringObservable()
        expiryDateObservable = expiryDateEditText.getBehaviorStringObservable().map { expiryString ->

            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            try {
                val expiryDate = LocalDate.parse(expiryString, formatter)
                expiryDate
            } catch (parseException: DateTimeParseException) {
                Timber.i("Invalid date format ${expiryString}")
                LocalDate.MIN
            }


        }.cache()

        creditCardNumberEditText.visibility = View.VISIBLE
        cvvEditText.visibility = View.VISIBLE
        expiryDateEditText.visibility = View.VISIBLE
        ibanEditText.visibility = View.GONE
        bicEditText.visibility = View.GONE
        registerSepaButton.isEnabled = false
        registerCreditCardButton.isEnabled = true

        methodSelectorGroup.setOnCheckedChangeListener{
            group, checkedId ->
            when(checkedId) {
                R.id.ccSelectedRadio -> {
                    creditCardNumberEditText.visibility = View.VISIBLE
                    cvvEditText.visibility = View.VISIBLE
                    expiryDateEditText.visibility = View.VISIBLE
                    ibanEditText.visibility = View.GONE
                    bicEditText.visibility = View.GONE
                    registerSepaButton.isEnabled = false
                    registerCreditCardButton.isEnabled = true
                }
                R.id.sepaSelectedRadio -> {
                    creditCardNumberEditText.visibility = View.GONE
                    cvvEditText.visibility = View.GONE
                    expiryDateEditText.visibility = View.GONE
                    ibanEditText.visibility = View.VISIBLE
                    bicEditText.visibility = View.VISIBLE
                    registerSepaButton.isEnabled = true
                    registerCreditCardButton.isEnabled = false
                }
            }
        }

        methodSelectorGroup.check(R.id.ccSelectedRadio)
    }

}