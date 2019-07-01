//package com.mobilabsolutions.payment.android.newapi;
//
//import com.mobilabsolutions.payment.android.psdk.PaymentSdk;
//import com.mobilabsolutions.payment.android.psdk.internal.PaymentSdkImpl;
//import com.mobilabsolutions.payment.android.psdk.model.BillingData;
//import com.mobilabsolutions.payment.android.psdk.model.CreditCardData;
//import com.mobilabsolutions.payment.android.psdk.RegistrationManager;
//import com.mobilabsolutions.payment.android.psdk.model.SepaData;
//
//import org.junit.Assert;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.Timeout;
//import org.junit.runner.RunWith;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//import org.threeten.bp.LocalDate;
//
//import java.util.concurrent.CountDownLatch;
//
//import io.reactivex.disposables.Disposable;
//import io.reactivex.schedulers.Schedulers;
//
//import static org.junit.Assert.assertEquals;
//import static org.powermock.api.mockito.PowerMockito.mock;
//import static org.powermock.api.mockito.PowerMockito.mockStatic;
//import static org.powermock.api.mockito.PowerMockito.when;
//
///**
// * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
// */
//
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(PaymentSdkImpl.class)
//public class NewApiRegistrationMockTest {
//
//    private CreditCardData validCreditCardData;
//    private SepaData validSepaData;
//    private BillingData validBillingData;
//
//
//    @Rule
//    public Timeout globalTimeout = Timeout.seconds(10);
//
//
//    @Before
//    public void setUp() {
//        mockStatic(PaymentSdkImpl.class);
//        when(PaymentSdk.getRegistrationManager()).thenReturn(new RegistrationManagerMock());
//
//        PaymentSdk.initalize("BS-KeyKey");
//        validCreditCardData = new CreditCardData();
//        validCreditCardData.setCvv("123");
//        validCreditCardData.setExpiryDate(LocalDate.of(2021,1,1));
//        validCreditCardData.setCvv("123");
//        validCreditCardData.setHolder("Holder Holderman");
//
//        validBillingData = new BillingData();
//        validBillingData.setCity("Cologne");
//        validBillingData.setEmail("holder@email.test");
//        validBillingData.setAddress1("Street 1");
//        validBillingData.setCountry("Germany");
//        validBillingData.setFirstName("Holder");
//        validBillingData.setLastName("Holderman");
//
//        validSepaData = new SepaData();
//        validSepaData.setBic("PBNKDEFF");
//        validSepaData.setIban("DE63123456791212121212");
//    }
//
//    @Test
//    public void testCardRegistration() {
//
//        CountDownLatch latch = new CountDownLatch(1);
//
//        RegistrationManager registrationManager = PaymentSdk.getRegistrationManager();
//        Disposable registrationDisposable = registrationManager.executePayoneRequest(
//                validCreditCardData,
//                validBillingData
//        )
//                .subscribeOn(Schedulers.io())
//                .subscribe(
//                        paymentAlias -> {
//                            Assert.assertNotNull(paymentAlias);
//                            assertEquals("1234-CC", paymentAlias);
//                            System.out.println("Payment aliasId: " + paymentAlias);
//                            latch.countDown();
//
//                        }
//                );
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        registrationDisposable.dispose();
//    }
//
//    @Test
//    public void testSepaRegistration() {
//        CountDownLatch latch = new CountDownLatch(1);
//
//        RegistrationManager registrationManager = PaymentSdk.getRegistrationManager();
//        Disposable registrationDisposable = registrationManager.registerSepaAccount(
//                validSepaData,
//                validBillingData
//        ).subscribeOn(Schedulers.io())
//                .subscribe(
//                        paymentAlias -> {
//                            Assert.assertNotNull(paymentAlias);
//                            assertEquals("1234-SEPA", paymentAlias);
//                            System.out.println("Payment aliasId: " + paymentAlias);
//                            latch.countDown();
//
//                        }
//                );
//
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        registrationDisposable.dispose();
//    }
//
//    @Test
//    public void testCreditCardRemoval() {
//
//    }
//
//
//
//
//
//}
