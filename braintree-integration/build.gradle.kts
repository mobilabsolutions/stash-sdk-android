plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
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
    implementation(Libs.AndroidX.constraintlayout)


    implementation(Libs.braintree)

    implementation(Libs.Dagger.dagger)
    kapt(Libs.Dagger.compiler)

    testImplementation(Libs.junit)
    kaptTest(Libs.Dagger.compiler)

    androidTestImplementation(Libs.AndroidX.appcompat)
    androidTestImplementation(Libs.AndroidX.constraintlayout)

    androidTestImplementation(Libs.AndroidX.Test.runner)
    androidTestImplementation(Libs.AndroidX.Test.espressoCore)
    androidTestImplementation(Libs.AndroidX.Test.espressoIntents)
    androidTestImplementation(Libs.AndroidX.Test.rules)
    androidTestImplementation(Libs.AndroidX.Test.uiAutomator)
    kaptAndroidTest(Libs.Dagger.compiler)

}
repositories {
    mavenCentral()
}
