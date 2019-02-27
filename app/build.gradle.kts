plugins {
    id("com.android.application")
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
        applicationId = "com.mobilabsolutions.payment.sample"
        minSdkVersion(PaymentSdkBuildConfigs.minSdk)
        targetSdkVersion(PaymentSdkBuildConfigs.targetSdk)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "mobilabBackendUrl", "\"" + propOrDefWithTravis(PaymentSdkRelease.mobilabBackendUrl, "") + "\"")
            buildConfigField("String", "oldBsTestKey", "\"" + propOrDefWithTravis(PaymentSdkRelease.oldBsTestKey, "") + "\"")
            buildConfigField("String", "oldBsApiUrl", "\"" + propOrDefWithTravis(PaymentSdkRelease.oldBsApiUrl, "") + "\"")
            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"
        }
        getByName("release") {
            buildConfigField("String", "mobilabBackendUrl", "\"" + propOrDefWithTravis(PaymentSdkRelease.mobilabBackendUrl, "") + "\"")
            buildConfigField("String", "oldBsTestKey", "\"" + propOrDefWithTravis(PaymentSdkRelease.oldBsTestKey, "") + "\"")
            buildConfigField("String", "oldBsApiUrl", "\"" + propOrDefWithTravis(PaymentSdkRelease.oldBsApiUrl, "") + "\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }


    dexOptions {
        preDexLibraries = !isTravisBuild
    }
}

dependencies {
    implementation(project(Modules.paymentSdk)) //Core
//    implementation(project(Modules.stripeIntegration))
    implementation(project(Modules.bsOldIntegration)) //BSOLD Module

    implementation(Libs.Kotlin.stdlib)

    implementation(Libs.AndroidX.constraintlayout)
    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.Google.material)

    implementation(Libs.timber)
    implementation(Libs.Stetho.stetho)
    implementation(Libs.Stetho.stethoOkhttp33)

    implementation(Libs.Dagger.dagger)
    implementation(Libs.Dagger.daggerAndroid)
    implementation(Libs.Dagger.androidSupport)
    kapt(Libs.Dagger.compiler)
    kapt(Libs.Dagger.androidProcessor)

    implementation(Libs.RxJava.rxKotlin)
    implementation(Libs.RxJava.rxJava)
    implementation(Libs.RxJava.rxAndroid)

    implementation(Libs.OkHttp.okhttp)
    implementation(Libs.OkHttp.loggingInterceptor)

    implementation(Libs.Retrofit.retrofit)
    implementation(Libs.Retrofit.gsonConverter)
    implementation(Libs.Retrofit.retrofit_rxjava_adapter)

    implementation("com.mobilabsolutions.android.commons:android-commons-dagger:3.0.1@aar")
    implementation("com.mobilabsolutions.android.commons:android-commons:3.0.0@aar")
}
