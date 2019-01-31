[![Build Status](https://travis-ci.com/mobilabsolutions/payment-sdk-android-open.svg?token=FD4eibz3gzcfCVXeJm9e&branch=master)](https://travis-ci.com/mobilabsolutions/payment-sdk-android-open)
# Android Payment SDK

This repository contains code providing Payment SDK API to Android platform

To get familiar with the overall Payment SDK project please visit [Common payment wiki](https://github.com/mobilabsolutions/payment-sdk-wiki-open/wiki)

To learn more about the Android Payment SDK architecture and flows please visit [Android SDK Wiki](https://github.com/mobilabsolutions/payment-sdk-android-open/wiki)

To set up the build on your machine you need to add certain variables to your `local.properties` file. Since these
contain private keys and other confidential data, please ask the project PO for access to them.

This repository contains multiple modules:
* `lib` - Core library module exposing SDK APIs, facilitating high level flows, and handling communication with Payment Backend
* `app` - Sample application using the payment SDK
* `*-integration` - Various PSP integration modules (Implementation in progress)

A normal use case for a third party developer would be to include `lib` and a specific integration module, i.e. `stripe-integration`

The follwoing integration and usage steps are pre-modularization and as such expect only `lib` module to be included in the project. This
read me will be updated to reflect changes once the integration modules are implemented completely.

#### Including the SDK in your project
TBD Repository serving the artefacts

`implementation com.mobilabsolutions.payment:lib:0.9.5`

#### Initializing the SDK
```
import com.mobilabsolutions.payment.android.psdk.PaymentSdk

PaymentSdk.initalize("PD-BS-TOKEN");
```

#### Credit card registration
```
CreditCardData creditCardData = new CreditCardData();
creditCardData.setNumber("4111111111111111");
creditCardData.setExpiryDate(LocalDate.of(2021,1,1));
creditCardData.setCvv("123");


RegistrationManager registrationManager = PaymentSdk.getRegistrationManager();
registrationManager.registerCreditCard(creditCardData, billingData)
        .subscribe(
                paymentAlias -> {
                    //Handle returned payment alias
                },
                error -> {
                    //Handle error
                }
        );
```

#### SEPA registration
```
SepaData sepaData = new SepaData();
sepaData.setBic("PBNKDEFF");
sepaData.setIban("DE63123456791212121212");
sepaData.setHolderName("Holder Holderman");
billingData = new billingData();

RegistrationManager registrationManager = PaymentSdk.getRegistrationManager();
registrationManager.registerSepa(sepaData, billingData)
        .subscribe(
                paymentAlias -> {
                    // Handle returned payment alias
                },
                error -> {
                    // Handle error
                }

        );
```
#### Executing credit card payment
```
PaymentData paymentData = new PaymentData();
paymentData.setAmount(100);
paymentData.setCurrency("EUR");
paymentData.setCustomerId("1");
paymentData.setReason("Test payment");

PaymentManager registrationManager = PaymentSdk.getPaymentManager();
registrationManager.executeCreditCardPaymentWithAlias(creditCardAlias, paymentData)
        .subscribe(
                transactionId -> {
                    // Handle transaction id
                },
                error -> {
                    // Handle error
                }
        );
```

#### Executing SEPA payment
```
PaymentData paymentData = new PaymentData();
paymentData.setAmount(100);
paymentData.setCurrency("EUR");
paymentData.setCustomerId("1");
paymentData.setReason("Test payment");

PaymentManager registrationManager = PaymentSdk.getPaymentManager();
registrationManager.executeSepaPaymentWithAlias(sepaAlias, paymentData)
        .subscribe(
                transactionId -> {
                    // Handle transaction id
                },
                error -> {
                    // Handle error
                }
        );

```

#### Executing a one time payment
```
CreditCardData creditCardData = new CreditCardData();
creditCardData.setNumber("4111111111111111");
creditCardData.setExpiryDate(LocalDate.of(2021,1,1));
creditCardData.setCvv("123");
creditCardData.setHolder("Holder Holderman");

PaymentData paymentData = new PaymentData();
paymentData.setAmount(100);
paymentData.setCurrency("EUR");
paymentData.setCustomerId("1");
paymentData.setReason("Test payment");

PaymentManager registrationManager = PaymentSdk.getPaymentManager();
registrationManager.executeCreditCardPayment(creditCardAlias, billingData, paymentData)
        .subscribe(
                transactionId -> {
                    // Handle transaction id
                },
                error -> {
                    // Handle error
                }
        );
```

#### Removing a credit card alias
```
RegistrationManager registrationManager = PaymentSdk.getRegistrationManager();
registrationManager.removeCreditCardAlias(alias)
                .subscribe(
                        () -> {
                            // Handle success
                        },
                        error -> {
                            // Handle error
                        }

                );
```

#### Removing a SEPA alias
```
RegistrationManager registrationManager = PaymentSdk.getRegistrationManager();
registrationManager.removeSepaAlias(alias)
                .subscribe(
                        () -> {
                            // Handle success
                        },
                        error -> {
                            // Handle error
                        }

                );
```