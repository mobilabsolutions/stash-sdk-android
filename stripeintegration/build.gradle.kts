plugins {
    id("com.android.library")
    kotlin("android")
}

val stripePublicKey = propOrDefWithTravis(PaymentSdkRelease.stripePublicKey, "")

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
            resValue("string", "stripe_public_key", "\""+stripePublicKey+"\"")
        }
        getByName("release") {
            isMinifyEnabled = false
            resValue("string", "stripe_public_key", "\""+stripePublicKey+"\"")
        }
    }

}

dependencies {
    implementation(project(Modules.paymentSdk))
    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.stripe)

    testImplementation(Libs.junit)

    androidTestImplementation(Libs.AndroidX.Test.runner)
    androidTestImplementation(Libs.AndroidX.Test.espressoCore)

}
repositories {
    mavenCentral()
}
