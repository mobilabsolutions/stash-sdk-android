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

## Supported Payment Service Providers - PSP

- BSPayone [Credit Cards / SEPA]
- Braintree [PayPal]
- Adyen [Credit Cards / SEPA]

### Including the SDK in your project

**Gradle**

`implementation com.mobilabsolutions.payment:lib:0.9.5`

**Gradle Kotlin DSL**

`implementation("com.mobilabsolutions.payment:lib:0.9.5")`

### Initializing the SDK



To use the SDK, you need to initialize it with some configuration data. Among the data that needs to be provided are the public key as well as the backend endpoint that should be used by the SDK.

To connect the SDK to a given payment service provider (PSP), you need to pass the IntegrationCompanion object to the SDK

Kotlin

```kotlin
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.PaymentSdkConfiguration
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreeIntegration
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration

val  configuration = PaymentSdkConfiguration(
        publicKey = "YourApiKey",
        endpoint = "https://payment-dev.mblb.net/api/",
        integrations = setOf(BsPayoneIntegration, BraintreeIntegration),
        testMode = true
)
PaymentSdk.initalize(this, configuration)
```

Java

```java
import com.mobilabsolutions.payment.android.psdk.PaymentSdk
import com.mobilabsolutions.payment.android.psdk.PaymentSdkConfiguration
import com.mobilabsolutions.payment.android.psdk.integration.braintree.BraintreeIntegration;
import com.mobilabsolutions.payment.android.psdk.integration.bspayone.BsPayoneIntegration;
import com.mobilabsolutions.payment.android.psdk.internal.psphandler.IntegrationCompanion;

Set<IntegrationCompanion> integrations = new HashSet<>();
integrations.add(BraintreeIntegration.Companion);
integrations.add(BsPayoneIntegration.Companion);

PaymentSdkConfiguration configuration = new PaymentSdkConfiguration.Builder("YourPublicKey")
        .setEndpoint("https://payment-dev.mblb.net/api/")
        .setIntegrations(integrations)
        .setTestMode(true)
        .build();

PaymentSdk.initalize(context, configuration);
``` 
#### Using the SDK in test mode

The payment SDK can also be used in so-called test mode. Transactions created there are not forwarded to the production PSP but rather to whatever sandboxing mode the PSP provides.
To configure the SDK to use test mode, simply prepend the `test.` subdomain to your endpoint URL (if the corresponding Load Balancer has been set up). Another method to instruct the SDK to use test mode while keeping the same URL is manually setting the `testMode` property on the `PaymentSdkConfiguration` used to configure the SDK.

For example:

| Test Mode | Production Mode |
| --------- | --------------- |
| https://test.payment.example.net/api/v1 | https://payment.example.net/api/v1 |

Or in code, you should supply testMode parameter when creating your `configuration` object, as shown in the previous section

#### Registering payment method using UI

#### Credit card registration

o register a credit card, the `registerCreditCard` method of the registration manager is used.
Provide it with an instance of `CreditCardData`, which upon initialization also validates the credit card data.

The `CreditCardData` is provided with `BillingData`. This `BillingData` contains information about the user that is necessary for registering a credit card. Its fields are all optional and their necessity PSP-dependant.

Kotlin

```kotlin
val creditCardData = CreditCardData(
    number = "4111111111111111",
    expiryDate = LocalDate.of(2021,1,1),
    cvv = "123"
)

val billingData = BillingData(
    city = "Cologne"
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

BillingData billingData = new BillingData.Builder()
         .setCity("Cologne")
         .build()


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

To register a SEPA account, we can use the `registerSepa` method of the registration manager. Here, as is the case for the credit card data, the billing data is optional and the values that need to be provided are PSP-dependant.

Kotlin 

```kotlin
val sepaData = SepaData(
    bic = "PBNKDEFF", 
    iban = "DE63123456791212121212",
    holderName = "Holder Holderman"
    )
    
val billingData = BillingData(
    city = "Cologne"
)

val registrationManager = PaymentSdk.getRegistrationManager();
registrationManager.registerSepa(sepaData, billingData)
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
BillingData billingData = new BillingData.Builder()
         .setCity("Cologne")
         .build()

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

### Using the module UI for adding a payment method

Since the PSP modules know best which data needs to be provided in which situation, it is also possible to offload the UI work for adding a payment method to them.
By calling `registerPaymentMethodUsingUI` on the registration manager, the user is shown a selection of possible payment methods types and then fields for creating payment methods of the selected type.

You can skip the payment method chosing screen by suppliying which method you want the user to enter immediately. Omitting this will show the payment method chooser

Kotlin 

```kotlin

val registrationManager = PaymentSdk.getRegistrationManager();
registrationManager.registerPaymentMehodUsingUi(activity, PaymentMethodType.CREDITCARD)
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

RegistrationManager registrationManager = PaymentSdk.getRegistrationManager();
registrationManager.registerPaymentMehodUsingUi(activity, PaymentMethodType.CREDITCARD, null) 
        .subscribe(
                paymentAlias -> {
                    // Handle returned payment alias
                },
                error -> {
                    // Handle error
                }

        );
``` 

### Idempotency
All calls provided by Payment SDK are idempotent. To use idempotency simply provide a UUID to with any of the registration methods used.

**Example**

Kotlin
```kotlin
val registrationIdempotencyKey = UUID.randomUUID()
val registrationManager = PaymentSdk.getRegistrationManager();
registrationManager.registerPaymentMehodUsingUi(activity, idempotencyKey = registrationIdempotencyKey)
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
UUID registrationIdempotencyKey = UUID.randomUUID();

RegistrationManager registrationManager = PaymentSdk.getRegistrationManager();
registrationManager.registerPaymentMehodUsingUi(activity, null, registrationIdempotencyKey) 
        .subscribe(
                paymentAlias -> {
                    // Handle returned payment alias
                },
                error -> {
                    // Handle error
                }

        );
``` 