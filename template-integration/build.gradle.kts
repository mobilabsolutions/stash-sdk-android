plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

val templatePublicKey = propOrDefWithTravis(PaymentSdkRelease.templatePublicKey, "")

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
            resValue("string", "template_public_key", "\""+templatePublicKey+"\"")
        }
        getByName("release") {
            isMinifyEnabled = false
            resValue("string", "template_public_key", "\""+templatePublicKey+"\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lintOptions {
        isAbortOnError = false
    }

}

dependencies {
    implementation(project(Modules.paymentSdk))
    implementation(Libs.Kotlin.stdlib)

    implementation(Libs.AndroidX.appcompat)

    implementation(Libs.Dagger.dagger)
    kapt(Libs.Dagger.compiler)

    testImplementation(Libs.junit)
    kaptTest(Libs.Dagger.compiler)

    androidTestImplementation(Libs.AndroidX.Test.runner)
    androidTestImplementation(Libs.AndroidX.Test.espressoCore)
    kaptAndroidTest(Libs.Dagger.compiler)

}
repositories {
    mavenCentral()
}

licenseReport {
    generateHtmlReport = true
    generateJsonReport = true

    copyHtmlReportToAssets = false
    copyJsonReportToAssets = false
}
