plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
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
    lintOptions {
        isAbortOnError = false
    }


    buildTypes {
        getByName("debug") {
            resValue("string", "template_public_key", "\""+templatePublicKey+"\"")
        }
        getByName("release") {
            isMinifyEnabled = false
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

    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.constraintlayout)
    implementation(Libs.Google.material)


    implementation(Libs.Utils.yearMonthPicker)


    implementation(Libs.Dagger.dagger)
    kapt(Libs.Dagger.compiler)

    testImplementation(Libs.junit)
    kaptTest(Libs.Dagger.compiler)

    testImplementation(Libs.junit)
    testImplementation(Libs.mockitoCore)
    testImplementation(Libs.mockwebserver)
    testImplementation(Libs.PowerMock.module)
    testImplementation(Libs.PowerMock.api)
    testImplementation(Libs.AndroidX.Test.core)
    kaptTest(Libs.Dagger.compiler)

    androidTestImplementation(project(Modules.paymentSdk))
    androidTestImplementation(Libs.junit)
    androidTestImplementation(Libs.mockitoCore)
    androidTestImplementation(Libs.mockwebserver)
    androidTestImplementation(Libs.AndroidX.Test.runner)
    androidTestImplementation(Libs.AndroidX.Test.espressoCore)
    androidTestImplementation(Libs.AndroidX.Test.core)
    kaptAndroidTest(Libs.Dagger.compiler)

}
repositories {
    mavenCentral()
}
