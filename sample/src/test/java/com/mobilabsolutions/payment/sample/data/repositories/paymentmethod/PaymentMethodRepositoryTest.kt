package com.mobilabsolutions.payment.sample.data.repositories.paymentmethod

import com.mobilabsolutions.payment.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Before

/**
 * @author [Yisuk Kim](yisuk@mobilabsolutions.com) on 23-04-2019.
 */
class PaymentMethodRepositoryTest {

    private lateinit var repository: PaymentMethodRepositoryImpl

    @Before
    fun setUp() {

    }

    @After
    fun tearDown() {
    }
}

val testCoroutineDispatchers = AppCoroutineDispatchers(Dispatchers.Main, Dispatchers.Main, Dispatchers.Main)