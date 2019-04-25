package com.mobilabsolutions.payment.android.psdk.integration.bspayone.uicomponents

// ktlint-disable no-wildcard-imports
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.R
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.PersonalDataValidator
import com.mobilabsolutions.payment.android.psdk.internal.uicomponents.SepaDataValidator
import com.mobilabsolutions.payment.android.psdk.model.BillingData
import com.mobilabsolutions.payment.android.psdk.model.SepaData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.*
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class SepaDataEntryFragment : Fragment() {

    @Inject
    lateinit var uiComponentHandler: UiComponentHandler

    @Inject
    lateinit var sepaDataValidator: SepaDataValidator

    @Inject
    lateinit var personalDataValidator: PersonalDataValidator

    lateinit var errorDrawable: Drawable
    lateinit var normalBacgroundDrawable: Drawable

    private val disposables = CompositeDisposable()
    private val firstNameSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val lastNameSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val ibanSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val countrySubject: BehaviorSubject<String> = BehaviorSubject.createDefault("Germany")

    private var viewState: SepaDataEntryViewState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BsPayoneIntegration.integration?.bsPayoneIntegrationComponent?.inject(this)
        Timber.d("Created")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sepa_data_entry_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorDrawable = resources.getDrawable(R.drawable.edit_text_frame_error)
        normalBacgroundDrawable = resources.getDrawable(R.drawable.edit_text_frame)

        disposables += Observables.combineLatest(
                firstNameSubject,
                lastNameSubject,
                ibanSubject,
                countrySubject,
                ::SepaDataEntryViewState)
                .subscribe(this::onViewStateNext)


        firstNameEditText.onTextChanged { firstNameSubject.onNext(it.toString().trim()) }
        lastNameEditText.onTextChanged { lastNameSubject.onNext(it.toString().trim()) }
        creditCardNumberEditText.onTextChanged { ibanSubject.onNext(it.toString().trim()) }
        countryText.onTextChanged { countrySubject.onNext(it.toString().trim()) }

        countryText.setOnClickListener {
            Timber.d("Country selector")
        }

        saveButton.setOnClickListener {
            viewState?.let {
                val dataMap: MutableMap<String, String> = mutableMapOf()
                dataMap.put(SepaData.FIRST_NAME, it.firstName)
                dataMap.put(SepaData.LAST_NAME, it.lastName)
                dataMap.put(SepaData.IBAN, it.iban)
                dataMap.put(BillingData.COUNTRY, it.country)
                uiComponentHandler.dataSubject.onNext(dataMap)
            }
        }
    }

    private fun onViewStateNext(state: SepaDataEntryViewState) {
        this.viewState = state
        var success = true
        success = validateFirstName(state.firstName) && success
        success = validateLastName(state.lastName) && success
        success = validateIban(state.iban) && success
        success = validateCountry(state.country) && success
        saveButton.isEnabled = success
    }

    private fun TextView.customError(message: String) {
        if (this.error == null) {
            this.setBackgroundResource(R.drawable.edit_text_frame_error)
            this.setError(message, ContextCompat.getDrawable(requireContext(), R.drawable.empty_drawable))
            this.invalidate()
        }
    }

    private fun TextView.clearError() {
        this.setBackgroundResource(R.drawable.edit_text_frame)
        this.error = null
    }

    private fun validateFirstName(name: String): Boolean {
        val validationResult = personalDataValidator.validateName(name)
        if (!validationResult.success) {
            firstNameEditText.customError(getString(validationResult.errorMessageResourceId))
        } else {
            firstNameEditText.clearError()
        }
        return validationResult.success
    }

    private fun validateLastName(name: String): Boolean {
        val validationResult = personalDataValidator.validateName(name)
        if (!validationResult.success) {
            lastNameEditText.customError(getString(validationResult.errorMessageResourceId))
        } else {
            lastNameEditText.clearError()
        }
        return validationResult.success
    }

    private fun validateIban(iban: String): Boolean {
        val validationResult = sepaDataValidator.validateIban(iban)
        if (!validationResult.success) {
            creditCardNumberEditText.setBackgroundResource(R.drawable.edit_text_frame_error)
            errorIban.visibility = View.VISIBLE
        } else {
            creditCardNumberEditText.setBackgroundResource(R.drawable.edit_text_frame)
            errorIban.visibility = View.GONE
        }
        return validationResult.success
    }

    private fun validateCountry(country: String): Boolean {
        return if (country.isNotEmpty()) {
            countryText.clearError()
            true
        } else {
            countryText.customError(getString(R.string.validation_error_missing_country))
            false
        }
    }

    data class SepaDataEntryViewState(
        val firstName: String = "",
        val lastName: String = "",
        val iban: String = "",
        val country: String = "Germany"
    )
}