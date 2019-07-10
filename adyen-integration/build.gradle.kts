/*
 * Copyright © MobiLab Solutions GmbH
 */

import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
}


android {
    compileSdkVersion(PaymentSdkBuildConfigs.compileSdk)
    buildToolsVersion(PaymentSdkBuildConfigs.buildtoolsVersion)

    defaultConfig {
        minSdkVersion(PaymentSdkBuildConfigs.minSdk)
        targetSdkVersion(PaymentSdkBuildConfigs.targetSdk)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "mobilabBackendUrl", "\"" + propOrDefWithTravis(PaymentSdkRelease.mobilabBackendUrl, "") + "\"")
            buildConfigField("String", "testPublishableKey", "\"" + propOrDefWithTravis(PaymentSdkRelease.testPublishableKey, "") + "\"")
        }
        getByName("release") {
            isMinifyEnabled = false
            buildConfigField("String", "mobilabBackendUrl", "\"" + propOrDefWithTravis(PaymentSdkRelease.mobilabBackendUrl, "") + "\"")
            buildConfigField("String", "testPublishableKey", "\"" + propOrDefWithTravis(PaymentSdkRelease.testPublishableKey, "") + "\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lintOptions {
        isAbortOnError = false
    }

    testOptions {
        unitTests.apply {
            isIncludeAndroidResources = true
            all(KotlinClosure1<Any, Test>({
                (this as Test).also {
                    maxHeapSize = "1024m"
                    testLogging {
                        events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED)
                    }
                }
            }, this))
        }
    }
}

dependencies {
    implementation(project(Modules.paymentSdk))
    implementation(Libs.Kotlin.stdlib)

    implementation(Libs.Adyen.base)
    implementation(Libs.Adyen.ui)
    implementation(Libs.Adyen.core)
    implementation(Libs.Adyen.coreCard)

    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.constraintlayout)
    implementation(Libs.Google.material)
    implementation(Libs.Dagger.dagger)
    kapt(Libs.Dagger.compiler)

    testImplementation(Libs.junit)
    testImplementation(Libs.robolectric)
    testImplementation(Libs.AndroidX.Test.core)
    kaptTest(Libs.Dagger.compiler)

    androidTestImplementation(project(Modules.paymentSdk))
    androidTestImplementation(Libs.junit)
    androidTestImplementation(Libs.mockitoCore)
    androidTestImplementation(Libs.AndroidX.Test.runner)
    androidTestImplementation(Libs.AndroidX.Test.espressoCore)
    androidTestImplementation(Libs.AndroidX.Test.core)
    kaptAndroidTest(Libs.Dagger.compiler)

}

licenseReport {
    generateHtmlReport = true
    generateJsonReport = true

    copyHtmlReportToAssets = false
    copyJsonReportToAssets = false
}
