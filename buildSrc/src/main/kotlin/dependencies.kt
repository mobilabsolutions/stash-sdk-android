import org.gradle.api.Project
import java.util.*

object PaymentSdkRelease {
    val travisBuildNumber = "TRAVIS_BUILD_NUMBER"
    val travisTag = "TRAVIS_TAG"
    val stripePublicKey = "STRIPE_PUBLI_CKEY"
    val mobilabBackendUrl = "BACKEND_DEVELOPMENT"
    val oldBsTestKey = "BS_TEST_PUBLIC_KEY"
    val oldBsApiUrl = "BS_TEST_API_URL"
    val oldBsExistingSepaAlias = "BS_EXISTING_SEPA_ALIAS"
    val oldBsExistingCcAlias = "BS_EXISTING_CC_ALIAS"
    val newBsApiUrl = "NEW_BS_API_URL"
    val newBsTestKey = "NEW_BS_TEST_PUBLIC_KEY"
    val hyperchargeTestKey = "HC_TEST_PUBLIC_KEY"

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

val isTravisBuild: Boolean = System.getenv("TRAVIS") == "true"

object Modules {
    val stripeIntegration = ":stripeintegration"
    val paymentSdk = ":lib"
}

object Libs {
    val androidGradlePlugin = "com.android.tools.build:gradle:3.3.0"

    val timber = "com.jakewharton.timber:timber:4.7.1"

    val stripe = "com.stripe:stripe-android:6.1.2"

    val junit = "junit:junit:4.12"

    val mockitoCore = "org.mockito:mockito-core:2.23.4"

    val simpleframework = "org.simpleframework:simple-xml:2.7.1"

    val threetenabp = "com.jakewharton.threetenabp:threetenabp:1.1.1"

    val mockwebserver = "com.squareup.okhttp3:mockwebserver:3.11.0"

    val iban4j = "org.iban4j:iban4j:3.2.1"

    object Google {
        val material = "com.google.android.material:material:1.1.0-alpha02"
        val crashlytics = "com.crashlytics.sdk.android:crashlytics:2.9.8"
        val fabricPlugin = "io.fabric.tools:gradle:1.27.0"
    }

    object Kotlin {
        private const val version = "1.3.11"
        val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
        val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
    }

    object Coroutines {
        private const val version = "1.1.0"
        val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        val rx2 = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:$version"
        val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    }

    object AndroidX {
        val appcompat = "androidx.appcompat:appcompat:1.0.2"
        val recyclerview = "androidx.recyclerview:recyclerview:1.0.0"

        object Test {
            val core = "androidx.test:core:1.1.0"
            val runner = "androidx.test:runner:1.1.1"
            val rules = "androidx.test:rules:1.1.1"

            val espressoCore = "androidx.test.espresso:espresso-core:3.1.1"
        }

        val archCoreTesting = "androidx.arch.core:core-testing:2.0.0"

        val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.0-alpha2"

        val coreKtx = "androidx.core:core-ktx:1.0.1"

        object Lifecycle {
            private const val version = "2.0.0"
            val extensions = "androidx.lifecycle:lifecycle-extensions:$version"
            val reactive = "androidx.lifecycle:lifecycle-reactivestreams:$version"
            val compiler = "androidx.lifecycle:lifecycle-compiler:$version"
        }

        object Room {
            private const val version = "2.0.0"
            val common = "androidx.room:room-common:$version"
            val runtime = "androidx.room:room-runtime:$version"
            val rxjava2 = "androidx.room:room-rxjava2:$version"
            val compiler = "androidx.room:room-compiler:$version"
        }
    }

    object RxJava {
        val rxJava = "io.reactivex.rxjava2:rxjava:2.2.5"
        val rxKotlin = "io.reactivex.rxjava2:rxkotlin:2.3.0"
        val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.1.0"
    }

    object Dagger {
        private const val version = "2.20"
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
        private const val version = "3.12.1"
        val okhttp = "com.squareup.okhttp3:okhttp:$version"
        val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    object Stetho {
        private const val version = "1.5.0"
        val stetho = "com.facebook.stetho:stetho:$version"
        val stethoOkhttp33 = "com.facebook.stetho:stetho-okhttp3:$version"
    }

    object PowerMock {
        private const val version = "2.0.0-beta.5"
        val module = "org.powermock:powermock-module-junit4:$version"
        val api = "org.powermock:powermock-api-mockito2:$version"
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
