package com.mobilabsolutions.payment.android.psdk.internal

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.mobilabsolutions.payment.android.psdk.CreditCardType
import com.mobilabsolutions.payment.android.psdk.ExtraAliasInfo
import com.mobilabsolutions.payment.android.psdk.PaymentMethodAlias
import com.mobilabsolutions.payment.android.psdk.PaymentMethodType
import com.mobilabsolutions.payment.android.psdk.exceptions.base.TemporaryException
import com.mobilabsolutions.payment.android.psdk.exceptions.validation.IdempotencyKeyInUseException
import com.mobilabsolutions.payment.android.psdk.internal.api.backend.RuntimeTypeAdapterFactory
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.ArgumentMatchers.nullable
import org.powermock.api.mockito.PowerMockito
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class IdempotencyTest {
    companion object {
        val extraInfoTypeAdapterFactory = RuntimeTypeAdapterFactory
            .of(ExtraAliasInfo::class.java, "extraType")
            .registerSubtype(ExtraAliasInfo.CreditCardExtraInfo::class.java, "CC")
            .registerSubtype(ExtraAliasInfo.SepaExtraInfo::class.java, "SEPA")
            .registerSubtype(ExtraAliasInfo.PaypalExtraInfo::class.java, "PAYPAL")

        val gson = GsonBuilder()
            .registerTypeAdapterFactory(extraInfoTypeAdapterFactory)
            .registerTypeHierarchyAdapter(Throwable::class.java, ThrowableSerializer())
            .create()
        val one: String = gson.toJson(IdempotencyData(System.currentTimeMillis(), PaymentMethodType.CC, null, TemporaryException("Temporary Exception")))
        const val KEY_1 = "36ac6a9c-2089-4e92-9ff9-111111111111"
        const val VALUE_1: String = "{\"timestamp\":1557152076418,\"paymentMethodType\":\"CC\",\"paymentMethodAlias\":{\"alias\":\"AliasOne\",\"paymentMethodType\":\"CC\", " +
            "\"extraAliasInfo\":{\"extraType\":\"CC\", \"creditCardMask\":\"VISA-1234\",\"expiryMonth\":10,\"expiryYear\":2020,\"creditCardType\":\"VISA\"}}}"
        const val KEY_2 = "36ac6a9c-2089-4e92-9ff9-222222222222"
        const val VALUE_2: String = "{\"timestamp\":1557153253969,\"paymentMethodType\":\"CC\",\"error\":{\"detailMessage\":\"Temporary Exception\"," +
            "\"stackTrace\":[{\"declaringClass\":\"com.mobilabsolutions.payment.android.psdk.internal.IdempotencyTest\",\"methodName\":\"\\u003cclinit\\u003e\"," +
            "\"fileName\":\"IdempotencyTest.kt\",\"lineNumber\":28},{\"declaringClass\":\"sun.reflect.NativeConstructorAccessorImpl\",\"methodName\":\"newInstance0\"," +
            "\"fileName\":\"NativeConstructorAccessorImpl.java\",\"lineNumber\":-2},{\"declaringClass\":\"sun.reflect.NativeConstructorAccessorImpl\",\"methodName\":\"newInstance\"," +
            "\"fileName\":\"NativeConstructorAccessorImpl.java\",\"lineNumber\":62},{\"declaringClass\":\"sun.reflect.DelegatingConstructorAccessorImpl\",\"methodName\":\"newInstance\"," +
            "\"fileName\":\"DelegatingConstructorAccessorImpl.java\",\"lineNumber\":45},{\"declaringClass\":\"java.lang.reflect.Constructor\",\"methodName\":\"newInstance\"," +
            "\"fileName\":\"Constructor.java\",\"lineNumber\":423},{\"declaringClass\":\"org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl\",\"methodName\":\"createTestInstance\"," +
            "\"fileName\":\"PowerMockJUnit44RunnerDelegateImpl.java\",\"lineNumber\":197},{\"declaringClass\":\"org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl\",\"methodName\":\"createTest\"," +
            "\"fileName\":\"PowerMockJUnit44RunnerDelegateImpl.java\",\"lineNumber\":182},{\"declaringClass\":\"org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl\",\"methodName\":\"invokeTestMethod\"," +
            "\"fileName\":\"PowerMockJUnit44RunnerDelegateImpl.java\",\"lineNumber\":204},{\"declaringClass\":\"org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl\",\"methodName\":\"runMethods\"," +
            "\"fileName\":\"PowerMockJUnit44RunnerDelegateImpl.java\",\"lineNumber\":160},{\"declaringClass\":\"org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl\$1\",\"methodName\":\"run\"," +
            "\"fileName\":\"PowerMockJUnit44RunnerDelegateImpl.java\",\"lineNumber\":134},{\"declaringClass\":\"org.junit.internal.runners.ClassRoadie\",\"methodName\":\"runUnprotected\"," +
            "\"fileName\":\"ClassRoadie.java\",\"lineNumber\":34},{\"declaringClass\":\"org.junit.internal.runners.ClassRoadie\",\"methodName\":\"runProtected\"," +
            "\"fileName\":\"ClassRoadie.java\",\"lineNumber\":44},{\"declaringClass\":\"org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl\",\"methodName\":\"run\"," +
            "\"fileName\":\"PowerMockJUnit44RunnerDelegateImpl.java\",\"lineNumber\":136},{\"declaringClass\":\"org.powermock.modules.junit4.common.internal.impl.JUnit4TestSuiteChunkerImpl\",\"methodName\":\"run\"," +
            "\"fileName\":\"JUnit4TestSuiteChunkerImpl.java\",\"lineNumber\":117},{\"declaringClass\":\"org.powermock.modules.junit4.common.internal.impl.AbstractCommonPowerMockRunner\",\"methodName\":\"run\"," +
            "\"fileName\":\"AbstractCommonPowerMockRunner.java\",\"lineNumber\":57},{\"declaringClass\":\"org.powermock.modules.junit4.PowerMockRunner\",\"methodName\":\"run\"," +
            "\"fileName\":\"PowerMockRunner.java\",\"lineNumber\":59},{\"declaringClass\":\"org.junit.runner.JUnitCore\",\"methodName\":\"run\"," +
            "\"fileName\":\"JUnitCore.java\",\"lineNumber\":137},{\"declaringClass\":\"com.intellij.junit4.JUnit4IdeaTestRunner\",\"methodName\":\"startRunnerWithArgs\"," +
            "\"fileName\":\"JUnit4IdeaTestRunner.java\",\"lineNumber\":68},{\"declaringClass\":\"com.intellij.rt.execution.junit.IdeaTestRunner\$Repeater\",\"methodName\":\"startRunnerWithArgs\"," +
            "\"fileName\":\"IdeaTestRunner.java\",\"lineNumber\":47},{\"declaringClass\":\"com.intellij.rt.execution.junit.JUnitStarter\",\"methodName\":\"prepareStreamsAndStart\"," +
            "\"fileName\":\"JUnitStarter.java\",\"lineNumber\":242},{\"declaringClass\":\"com.intellij.rt.execution.junit.JUnitStarter\",\"methodName\":\"main\"," +
            "\"fileName\":\"JUnitStarter.java\",\"lineNumber\":70}],\"suppressedExceptions\":[]}}"
    }

    internal var application = PowerMockito.mock(Application::class.java)
    lateinit var sharedPrefs: SharedPreferences
    lateinit var mockedEditor: SharedPreferences.Editor

    @Before
    fun setUp() {
        sharedPrefs = PowerMockito.mock(SharedPreferences::class.java)
        mockedEditor = PowerMockito.mock(SharedPreferences.Editor::class.java)

        PowerMockito.`when`(application.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs)
        PowerMockito.`when`(sharedPrefs.edit()).thenReturn(mockedEditor)
        PowerMockito.`when`(sharedPrefs.getString(anyString(), nullable(String::class.java))).thenReturn(null)
        PowerMockito.`when`(sharedPrefs.getString(eq(KEY_1), nullable(String::class.java))).thenReturn(VALUE_1)
        PowerMockito.`when`(sharedPrefs.getString(eq(KEY_2), nullable(String::class.java))).thenReturn(VALUE_2)
        PowerMockito.`when`(mockedEditor.commit()).thenReturn(true)
        PowerMockito.`when`(mockedEditor.putString(anyString(), anyString())).thenReturn(mockedEditor)
    }

    @Test
    fun testRegistrationSuccess() {
        val idempotencyKey = "36ac6a9c-2089-4e92-9ff9-245e89dace06"
        val observer = TestObserver<PaymentMethodAlias>()

        IdempotencyManager(gson, sharedPrefs).verifyIdempotencyAndContinue(idempotencyKey, PaymentMethodType.CC) {
            Single.just(PaymentMethodAlias("AnAlias", PaymentMethodType.CC,
                ExtraAliasInfo.CreditCardExtraInfo(
                    "VISA-1234",
                    10,
                    2020,
                    CreditCardType.VISA
                )))
        }.subscribe(observer)

        observer
            .assertValue { it.alias == "AnAlias" }
    }

    @Test
    fun testRegistrationFailure() {
        val idempotencyKey = "36ac6a9c-2089-4e92-9ff9-245e89dace06"
        val observer = TestObserver<PaymentMethodAlias>()

        IdempotencyManager(gson, sharedPrefs).verifyIdempotencyAndContinue(idempotencyKey, PaymentMethodType.CC) {
            Single.error(TemporaryException("Temporary Exception"))
        }.subscribe(observer)

        observer
            .assertError(TemporaryException::class.java)
    }

    @Test
    fun testRegistrationSuccessWithIdempotency() {
        val observer = TestObserver<PaymentMethodAlias>()

        IdempotencyManager(gson, sharedPrefs).verifyIdempotencyAndContinue(KEY_1, PaymentMethodType.CC) {
            Single.just(PaymentMethodAlias("AliasTwo", PaymentMethodType.CC,
                ExtraAliasInfo.CreditCardExtraInfo(
                    "VISA-1234",
                    10,
                    2020,
                    CreditCardType.VISA
                )))
        }.subscribe(observer)

        observer
            .assertValue {
                System.out.println(one)
                it.alias == "AliasOne"
            }
    }

    @Test
    fun testRegistrationFailureWithIdempotency() {
        val observer = TestObserver<PaymentMethodAlias>()

        IdempotencyManager(gson, sharedPrefs).verifyIdempotencyAndContinue(KEY_2, PaymentMethodType.CC) {
            Single.just(PaymentMethodAlias("AliasTwo", PaymentMethodType.CC,
                ExtraAliasInfo.CreditCardExtraInfo(
                    "VISA-1234",
                    10,
                    2020,
                    CreditCardType.VISA
                )))
        }.subscribe(observer)

        observer
            .assertError(Throwable::class.java)
    }

    @Test
    fun testIdempotencyKeyInUseExceptionWithIdempotency() {
        val observer = TestObserver<PaymentMethodAlias>()

        IdempotencyManager(gson, sharedPrefs).verifyIdempotencyAndContinue(KEY_1, PaymentMethodType.SEPA) {
            Single.just(PaymentMethodAlias("AliasTwo", PaymentMethodType.CC,
                ExtraAliasInfo.CreditCardExtraInfo(
                    "VISA-1234",
                    10,
                    2020,
                    CreditCardType.VISA
                )))
        }.subscribe(observer)

        observer
            .assertError(IdempotencyKeyInUseException::class.java)
    }
}