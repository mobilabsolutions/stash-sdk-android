package com.mobilabsolutions.payment.sample.payments

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.mobilabsolutions.payment.sample.R
import com.mobilabsolutions.payment.sample.core.BaseActivity
import com.mobilabsolutions.payment.sample.databinding.ActivityPaymentBinding
import com.mobilabsolutions.payment.sample.payments.selectpayment.SelectPaymentFragment

class PaymentActivity : BaseActivity() {

    companion object {
        const val PAY_AMOUNT_EXTRA = "PAY_AMOUNT_EXTRA"
    }

    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)

        val amount = intent.getIntExtra(PAY_AMOUNT_EXTRA, 0)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.select_payment_container,
                    SelectPaymentFragment.newInstance(amount))
                .commit()
        }
    }
}
