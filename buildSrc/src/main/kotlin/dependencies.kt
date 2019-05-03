import org.gradle.api.Project
import java.util.Properties

object PaymentSdkRelease {
    val travisBuildNumber = "TRAVIS_BUILD_NUMBER"
    val travisTag = "TRAVIS_TAG"
    val stripePublicKey = "STRIPE_PUBLIC_KEY"
    val mobilabBackendUrl = "BACKEND_DEVELOPMENT"
    val oldBsTestKey = "BS_TEST_PUBLIC_KEY"
    val oldBsApiUrl = "BS_TEST_API_URL"
    val oldBsExistingSepaAlias = "BS_EXISTING_SEPA_ALIAS"
    val oldBsExistingCcAlias = "BS_EXISTING_CC_ALIAS"
    val newBsApiUrl = "NEW_BS_API_URL"
    val newBsTestKey = "NEW_BS_TEST_PUBLIC_KEY"
    val hyperchargeTestKey = "HC_TEST_PUBLIC_KEY"

    val braintreeSandboxToken = "BRAINTREE_SANDBOX_TOKEN"

    val mobilabNexusUser = "MOBILAB_NEXUS_USER"
    val mobilabNexusPassword = "MOBILAB_NEXUS_PASSWORD"
}

object PaymentSdkBuildConfigs {
    val compileSdk = 28
    val minSdk = 21
    val targetSdk = 28
    val buildtoolsVersion = "28.0.3"
    val vapianoVersionCode = "1"
    val vapianoVersionName = "1.2.0"
}

object DemoRelease {
    val fabricApiKey = "FABRIC_API_KEY"
    val versionCode = "1"
    val versionName = "0.0.1"
}

val isTravisBuild: Boolean = System.getenv("TRAVIS") == "true"

object Modules {
    val stripeIntegration = ":stripeintegration"
    val bsOldIntegration = ":bs-old-integration"
    val bsPayoneIntegration = ":bspayone-integration"
    val braintreeIntegration = ":braintree-integration"
    val adyenIntegration = ":adyen-integration"
    val paymentSdk = ":lib"
}

object Libs {
    val androidGradlePlugin = "com.android.tools.build:gradle:3.4.0"

    val gradleVersionsPlugin = "com.github.ben-manes:gradle-versions-plugin:0.21.0"

    val timber = "com.jakewharton.timber:timber:4.7.1"

    val stripe = "com.stripe:stripe-android:6.1.2"

    val junit = "junit:junit:4.12"

    val mockitoCore = "org.mockito:mockito-core:2.27.0"

    val simpleframework = "org.simpleframework:simple-xml:2.7.1"

    val threetenabp = "com.jakewharton.threetenabp:threetenabp:1.2.0"

    val mockwebserver = "com.squareup.okhttp3:mockwebserver:3.14.1"

    val iban4j = "org.iban4j:iban4j:3.2.1"

    val braintree = "com.braintreepayments.api:braintree:3.0.0"

    val adyenCheckoutBase = "com.adyen.checkout:base:2.4.3"
    val adyenUi = "com.adyen.checkout:ui:2.4.3"
    val adyenCore = "com.adyen.checkout:core:2.4.3"
    val adyenCardCore = "com.adyen.checkout:core-card:2.4.3"


    val mvrx = "com.airbnb.android:mvrx:1.0.0"

    object Google {
        val material = "com.google.android.material:material:1.1.0-alpha05"
        val crashlytics = "com.crashlytics.sdk.android:crashlytics:2.9.9"
        val fabricPlugin = "io.fabric.tools:gradle:1.28.1"
    }

    object Kotlin {
        private const val version = "1.3.31"
        val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
        val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
    }

    object Coroutines {
        private const val version = "1.2.1"
        val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        val rx2 = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:$version"
        val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    }

    object AndroidX {
        val appcompat = "androidx.appcompat:appcompat:1.0.2"
        val recyclerview = "androidx.recyclerview:recyclerview:1.0.0"
        val cardview = "androidx.cardview:cardview:1.0.0"
        val archCoreTesting = "androidx.arch.core:core-testing:2.0.0"
        val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.0-alpha4"
        val coreKtx = "androidx.core:core-ktx:1.0.1"

        object Navigation {
            private const val version = "2.0.0"
            val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            val ui = "androidx.navigation:navigation-ui-ktx:$version"
            val safeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:$version"
        }

        object Test {
            val core = "androidx.test:core:1.1.1-alpha02"
            val coreKtx = "androidx.test:core:1.1.1-alpha02"
            val ext = "androidx.test.ext:junit-ktx:1.1.1-alpha02"
            val runner = "androidx.test:runner:1.1.1"
            val rules = "androidx.test:rules:1.1.1"

            val espressoCore = "androidx.test.espresso:espresso-core:3.2.0-alpha02"
            val espressoIntents = "androidx.test.espresso:espresso-intents:3.2.0-alpha02"

            val uiAutomator = "androidx.test.uiautomator:uiautomator:2.2.0"
        }

        object Lifecycle {
            private const val version = "2.0.0"
            val extensions = "androidx.lifecycle:lifecycle-extensions:$version"
            val reactivestreams = "androidx.lifecycle:lifecycle-reactivestreams:$version"
            val compiler = "androidx.lifecycle:lifecycle-compiler:$version"
        }

        object Room {
            private const val version = "2.1.0-alpha06"
            val common = "androidx.room:room-common:$version"
            val runtime = "androidx.room:room-runtime:$version"
            val rxjava2 = "androidx.room:room-rxjava2:$version"
            val compiler = "androidx.room:room-compiler:$version"
            val ktx = "androidx.room:room-ktx:$version"
            val testing = "androidx.room:room-testing:$version"
        }
    }

    object RxJava {
        val rxJava = "io.reactivex.rxjava2:rxjava:2.2.8"
        val rxKotlin = "io.reactivex.rxjava2:rxkotlin:2.3.0"
        val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
    }

    object Dagger {
        private const val version = "2.22.1"
        val dagger = "com.google.dagger:dagger:$version"
        val daggerAndroid = "com.google.dagger:dagger-android:$version"
        val androidSupport = "com.google.dagger:dagger-android-support:$version"
        val compiler = "com.google.dagger:dagger-compiler:$version"
        val androidProcessor = "com.google.dagger:dagger-android-processor:$version"
    }

    object Retrofit {
        private const val version = "2.5.0"
        val retrofit = "com.squareup.retrofit2:retrofit:$version"
        val retrofit_rxjava_adapter = "com.squareup.retrofit2:adapter-rxjava2:$version"
        val gsonConverter = "com.squareup.retrofit2:converter-gson:$version"
        val simplexmlConverter = "com.squareup.retrofit2:converter-simplexml:$version"
    }

    object OkHttp {
        private const val version = "3.14.1"
        val okhttp = "com.squareup.okhttp3:okhttp:$version"
        val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    object Stetho {
        private const val version = "1.5.1"
        val stetho = "com.facebook.stetho:stetho:$version"
        val stethoOkhttp33 = "com.facebook.stetho:stetho-okhttp3:$version"
    }

    object PowerMock {
        private const val version = "2.0.2"
        val module = "org.powermock:powermock-module-junit4:$version"
        val api = "org.powermock:powermock-api-mockito2:$version"
    }

    object AssistedInject {
        private const val version = "0.4.0"
        val annotationDagger2 = "com.squareup.inject:assisted-inject-annotations-dagger2:$version"
        val processorDagger2 = "com.squareup.inject:assisted-inject-processor-dagger2:$version"
    }

    object Epoxy {
        private const val version = "3.4.2"
        val epoxy = "com.airbnb.android:epoxy:$version"
        val dataBinding = "com.airbnb.android:epoxy-databinding:$version"
        val processor = "com.airbnb.android:epoxy-processor:$version"
    }

    object Utils {
        val commonsValidator = "commons-validator:commons-validator:1.6"
        val yearMonthPicker = "com.whiteelephant:monthandyearpicker:1.3.0"
    }
}

fun Project.propOrDefWithTravis(propertyName: String, defaultValue: String): String {
    val propertyValue: String?
    propertyValue = if (isTravisBuild) {
        System.getenv(propertyName)
    } else {
        try {
            val properties = Properties()
            properties.load(rootProject.file("local.properties").inputStream())
            properties.getProperty(propertyName)
        } catch (e: Exception) {
            null
        }
    }
    return if (propertyValue == null || propertyValue.isEmpty()) defaultValue else propertyValue
}
