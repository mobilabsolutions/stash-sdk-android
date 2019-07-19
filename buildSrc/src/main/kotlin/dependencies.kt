/*
 * Copyright © MobiLab Solutions GmbH
 */

import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.util.*

object PaymentSdkRelease {
    val travisBuildNumber = "TRAVIS_BUILD_NUMBER"
    val travisTag = "TRAVIS_TAG"
    val templatePublishableKey = "TEMPLATE_PUBLIC_KEY"
    val mobilabBackendUrl = "BACKEND_DEVELOPMENT"
    val newBsApiUrl = "NEW_BS_API_URL"
    val testPublishableKey = "NEW_BS_TEST_PUBLIC_KEY"
    val MobilabNexusUsername = "MOBILAB_NEXUS_USER"
    val MobilabNexusPassword = "MOBILAB_NEXUS_PASSWORD"
}

object PaymentSdkBuildConfigs {
    val compileSdk = 28
    val minSdk = 21
    val targetSdk = 28
    val buildtoolsVersion = "29.0.0"
}

object DemoRelease {
    val fabricApiKey = "FABRIC_API_KEY"
    val versionCode = "1"
    val versionName = "0.11" // 0.<Sprint number>
}

val isTravisBuild: Boolean = System.getenv("TRAVIS") == "true"

val isTravisTag: Boolean = !System.getenv("TRAVIS_TAG").isNullOrBlank()

object Modules {
    val templateIntegration = ":template-integration"
    val bsPayoneIntegration = ":bspayone-integration"
    val braintreeIntegration = ":braintree-integration"
    val adyenIntegration = ":adyen-integration"
    val paymentSdk = ":lib"
}

object Libs {
    val androidGradlePlugin = "com.android.tools.build:gradle:3.4.1"
    val gradleVersionsPlugin = "com.github.ben-manes:gradle-versions-plugin:0.21.0"
    val timber = "com.jakewharton.timber:timber:4.7.1"
    val junit = "junit:junit:4.12"
    val mockitoCore = "org.mockito:mockito-core:2.28.2"
    val robolectric = "org.robolectric:robolectric:4.3"
    val threetenabp = "com.jakewharton.threetenabp:threetenabp:1.2.1"
    val mockwebserver = "com.squareup.okhttp3:mockwebserver:3.12.0"
    val iban4j = "org.iban4j:iban4j:3.2.1"
    val braintree = "com.braintreepayments.api:braintree:3.1.0"
    val mvrx = "com.airbnb.android:mvrx:1.0.2"
    val caligraphy = "io.github.inflationx:calligraphy3:3.1.1"
    val viewPump = "io.github.inflationx:viewpump:2.0.3"
    val licencePlugin = "com.jaredsburrows:gradle-license-plugin:0.8.5"
    val dokkaPlugin = "org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.18"

    object Adyen {
        private const val version = "2.4.5"
        val base = "com.adyen.checkout:base:$version"
        val ui = "com.adyen.checkout:ui:$version"
        val core = "com.adyen.checkout:core:$version"
        val coreCard = "com.adyen.checkout:core-card:$version"
    }

    object Google {
        val material = "com.google.android.material:material:1.1.0-alpha07"
        val crashlytics = "com.crashlytics.sdk.android:crashlytics:2.10.1"
        val fabricPlugin = "io.fabric.tools:gradle:1.29.0"
    }

    object Kotlin {
        private const val version = "1.3.40"
        val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
        val test = "org.jetbrains.kotlin:kotlin-test-junit:$version"
    }

    object Coroutines {
        private const val version = "1.2.1"
        val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        val rx2 = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:$version"
        val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    }

    object AndroidX {
        val appcompat = "androidx.appcompat:appcompat:1.1.0-beta01"
        val recyclerview = "androidx.recyclerview:recyclerview:1.1.0-alpha06"
        val cardview = "androidx.cardview:cardview:1.0.0"
        val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta2"
        val coreKtx = "androidx.core:core-ktx:1.2.0-alpha02"

        object Navigation {
            private const val version = "2.1.0-alpha05"
            val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            val ui = "androidx.navigation:navigation-ui-ktx:$version"
            val safeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:$version"
        }

        object Test {
            val core = "androidx.test:core:1.2.1-alpha01"
            val coreKtx = "androidx.test:core:1.2.0"
            val ext = "androidx.test.ext:junit-ktx:1.1.2-alpha01"
            val runner = "androidx.test:runner:1.3.0-alpha01"
            val rules = "androidx.test:rules:1.3.0-alpha01"

            val espressoCore = "androidx.test.espresso:espresso-core:3.3.0-alpha01"
            val espressoIntents = "androidx.test.espresso:espresso-intents:3.3.0-alpha01"

            val uiAutomator = "androidx.test.uiautomator:uiautomator:2.2.0"
        }

        object Lifecycle {
            private const val version = "2.2.0-alpha01"
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
        }
    }

    object RxJava {
        val rxJava = "io.reactivex.rxjava2:rxjava:2.2.10"
        val rxKotlin = "io.reactivex.rxjava2:rxkotlin:2.3.0"
        val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
    }

    object Dagger {
        private const val version = "2.23.2"
        val dagger = "com.google.dagger:dagger:$version"
        val daggerAndroid = "com.google.dagger:dagger-android:$version"
        val androidSupport = "com.google.dagger:dagger-android-support:$version"
        val compiler = "com.google.dagger:dagger-compiler:$version"
        val androidProcessor = "com.google.dagger:dagger-android-processor:$version"
    }

    object Retrofit {
        private const val version = "2.6.0"
        val retrofit = "com.squareup.retrofit2:retrofit:$version"
        val retrofit_rxjava_adapter = "com.squareup.retrofit2:adapter-rxjava2:$version"
        val gsonConverter = "com.squareup.retrofit2:converter-gson:$version"
    }

    object OkHttp {
        private const val version = "4.0.0-alpha02"
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
        private const val version = "3.6.0"
        val epoxy = "com.airbnb.android:epoxy:$version"
        val dataBinding = "com.airbnb.android:epoxy-databinding:$version"
        val processor = "com.airbnb.android:epoxy-processor:$version"
    }

    object Utils {
        val commonsValidator = "commons-validator:commons-validator:1.6"
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

fun String.runCommand(project: Project): String {
    val command = this
    val output = ByteArrayOutputStream()
    project.exec {
        this.workingDir = project.rootDir
        this.commandLine = command.split(" ")
        this.standardOutput = output
    }
    return String(output.toByteArray()).trim()
}