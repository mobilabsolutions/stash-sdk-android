package com.mobilabsolutions.payment.sample.payments

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.mobilabsolutions.payment.sample.R
import com.mobilabsolutions.payment.sample.core.BaseActivity
import com.mobilabsolutions.payment.sample.databinding.ActivityPaymentBinding
import com.mobilabsolutions.payment.sample.payments.selectpayment.SelectPaymentFragment


class PaymentActivity : BaseActivity() {

    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.select_payment_container, SelectPaymentFragment())
                .commit()
        }
    }
}
