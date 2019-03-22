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

Gradle

`implementation com.mobilabsolutions.payment:lib:0.9.5`

Gradle Kotlin DSL

`implementation("com.mobilabsolutions.payment:lib:0.9.5")`

#### Initializing the SDK

Kotlin 

```kotlin
import com.mobilabsolutions.payment.android.psdk.PaymentSdk

PaymentSdk.initalize("PD-BS-TOKEN", PspIntegration);
```

Java 

```java
import com.mobilabsolutions.payment.android.psdk.PaymentSdk;

PaymentSdk.initalize("PD-BS-TOKEN", PspIntegration.create());
```

#### Credit card registration

Kotlin

```kotlin
val creditCardData = CreditCardData(
    number = "4111111111111111",
    expiryDate = LocalDate.of(2021,1,1),
    cvv = "123"
)


val registrationManager = PaymentSdk.getRegistrationManager();
registrationManager.registerCreditCard(creditCardData, billingData)
        .subscribeBy(
                onSuccess = {paymentAlias ->
                    //Handle returned payment alias
                },
                onError = {
                    //Handle error
                }              
               
        )
```

Java

```java
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

Kotlin 

```kotlin
val sepaData = SepaData(
    bic = "PBNKDEFF", 
    iban = "DE63123456791212121212",
    holderName = "Holder Holderman"
    )


val registrationManager = PaymentSdk.getRegistrationManager();
registrationManager.registerSepa(sepaData)
        .subscribeBy(
                onSuccess = {
                    //Handle returned payment alias
                },
                onError = {
                    // Handle error
                }

        );
```

Java

```java
SepaData sepaData = new SepaData();
sepaData.setBic("PBNKDEFF");
sepaData.setIban("DE63123456791212121212");
sepaData.setHolderName("Holder Holderman");
BillingData billingData = new BillingData.empty();

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



