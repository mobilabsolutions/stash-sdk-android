// package com.mobilabsolutions.payment.android.newapi
//
// import android.app.Application
// import android.content.SharedPreferences
// import com.mobilabsolutions.payment.android.psdk.PaymentSdk
// import com.mobilabsolutions.payment.android.psdk.exceptions.validation.InvalidApplicationContextException
// import com.mobilabsolutions.payment.android.psdk.exceptions.validation.InvalidPublishableKeyException
// import org.junit.Before
// import org.junit.Rule
// import org.junit.Test
// import org.junit.rules.ExpectedException
// import org.junit.runner.RunWith
// import org.mockito.ArgumentMatchers
// import org.powermock.api.mockito.PowerMockito
// import org.powermock.core.classloader.annotations.PowerMockIgnore
// import org.powermock.modules.junit4.PowerMockRunner
//
// /**
// * @author [Ugi](ugi@mobilabsolutions.com)
// */
// @RunWith(PowerMockRunner::class)
// @PowerMockIgnore("javax.net.ssl.*", "android.util.Log.**")
// class NewApiInitializationMockTest {
//
//    @Rule
//    var expectedException = ExpectedException.none()
//
//    internal var application = PowerMockito.mock(Application::class.java)
//    lateinit var sharedPreferences: SharedPreferences
//    lateinit var mockedEditor: SharedPreferences.Editor
//
//    @Before
//    fun setUp() {
//        sharedPreferences = PowerMockito.mock(SharedPreferences::class.java)
//        mockedEditor = PowerMockito.mock(SharedPreferences.Editor::class.java)
//
//        PowerMockito.`when`(application.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(sharedPreferences)
//        PowerMockito.`when`(sharedPreferences.edit()).thenReturn(mockedEditor)
//        PowerMockito.`when`(mockedEditor.commit()).thenReturn(true)
//        PowerMockito.`when`(mockedEditor.putString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(mockedEditor)
//    }
//
//    @Test
//    fun testInitializationLengthFailure() {
//        expectedException.expect(InvalidPublishableKeyException::class.java)
//        expectedException.expectMessage("Publishable key length is not valid")
//        PaymentSdk.initalize("", application)
//    }
//
//    @Test
//    fun testInitializationNullFailure() {
//        expectedException.expect(InvalidPublishableKeyException::class.java)
//        expectedException.expectMessage("Publishable key not supplied")
//        PaymentSdk.initalize(null, application)
//    }
//
//    @Test
//    fun testInitializationContextNullFailure() {
//        expectedException.expect(InvalidApplicationContextException::class.java)
//        expectedException.expectMessage("Application context not supplied")
//        PaymentSdk.initalize("120909090989898883", null)
//    }
//
//    @Test
//    fun testUninitializedSdkUsageFailure() {
//        expectedException.expect(RuntimeException::class.java)
//        expectedException.expectMessage("Payment SDK is not initialized, make sure you called initialize method before using")
//        PaymentSdk.getRegistrationManager()
//    }
//
//    @Test
//    fun testInitializationSuccess() {
//        PaymentSdk.initalize("PD-BS-eiXDb...doesn'tmatter...", application)
//    }
//
//
// }
