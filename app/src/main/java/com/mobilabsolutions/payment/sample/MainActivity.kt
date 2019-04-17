package com.mobilabsolutions.payment.sample

import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.mobilabsolutions.commonsv3_dagger.mvp.view.BaseNoPresenterFragmentActivity
import com.mobilabsolutions.payment.sample.payment.PaymentFragment
import com.mobilabsolutions.payment.sample.registration.RegistrationFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseNoPresenterFragmentActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    val REGISTRATION_FRAGMENT_TAG = RegistrationFragment::class.java.simpleName
    val PAYMENT_FRAGMENT_TAG = PaymentFragment::class.java.simpleName

//    @Inject
    lateinit var registrationFragment: RegistrationFragment

//    @Inject
    lateinit var paymentFragment: PaymentFragment

    val compositeDisposable = CompositeDisposable()

    override val contentId: Int = R.id.fragmentContainer

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return supportFragmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            registrationFragment = RegistrationFragment()
            paymentFragment = PaymentFragment()
            showRegistrationScreen()
        } else {
            val foundRegistrationFragment = fragmentManager.findFragmentByTag(REGISTRATION_FRAGMENT_TAG)
            registrationFragment = if (foundRegistrationFragment != null) {
                foundRegistrationFragment as RegistrationFragment
            } else {
                RegistrationFragment()
            }

            val foundPaymentFragment = fragmentManager.findFragmentByTag(PAYMENT_FRAGMENT_TAG)
            paymentFragment = if (foundPaymentFragment != null) {
                foundPaymentFragment as PaymentFragment
            } else {
                PaymentFragment()
            }
        }

        registrationButton.setOnClickListener {
            showRegistrationScreen()
        }

        paymentButton.setOnClickListener {
            showPaymentScreen()
        }
    }

    fun showRegistrationScreen() {
        replaceFragment(registrationFragment, REGISTRATION_FRAGMENT_TAG)
    }

    fun showPaymentScreen() {
        replaceFragment(paymentFragment, PAYMENT_FRAGMENT_TAG)
    }
}

fun EditText.getString() = this.getText().toString()

fun EditText.getStringObservable() = Observable.create<String> {
    it.onNext(this.editableText.toString())
    this.addTextChangedListener(
            object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    it.onNext(s.toString())
                }
            }
    )
}

fun EditText.getBehaviorStringObservable() = Observable.create<String> {
    it.onNext(this.editableText.toString())
    this.addTextChangedListener(
            object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    it.onNext(s.toString())
                }
            }
    )
}.replay(1).autoConnect()
