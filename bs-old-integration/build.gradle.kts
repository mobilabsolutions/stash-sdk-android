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

}

dependencies {
    implementation(project(Modules.paymentSdk))
    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.stripe)



    implementation(Libs.Dagger.dagger)
    kapt(Libs.Dagger.compiler)

    testImplementation(Libs.junit)
    kaptTest(Libs.Dagger.compiler)

    androidTestImplementation(project(Modules.paymentSdk))
    androidTestImplementation(Libs.AndroidX.Test.runner)
    androidTestImplementation(Libs.AndroidX.Test.espressoCore)
    kaptAndroidTest(Libs.Dagger.compiler)

}
repositories {
    mavenCentral()
}
