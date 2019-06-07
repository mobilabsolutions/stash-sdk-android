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
            buildConfigField("String", "newBsApiKey", "\"" + propOrDefWithTravis(PaymentSdkRelease.newBsTestKey, "") + "\"")
            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"
        }
        getByName("release") {
            buildConfigField("String", "mobilabBackendUrl", "\"" + propOrDefWithTravis(PaymentSdkRelease.mobilabBackendUrl, "") + "\"")
            buildConfigField("String", "newBsApiKey", "\"" + propOrDefWithTravis(PaymentSdkRelease.newBsTestKey, "") + "\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }


    dexOptions {
        preDexLibraries = !isTravisBuild
    }

    lintOptions {
        isAbortOnError = false
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation(project(Modules.paymentSdk)) //Core
    implementation(project(Modules.bsPayoneIntegration))
    implementation(project(Modules.braintreeIntegration))
    implementation(project(Modules.adyenIntegration))

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

}

licenseReport {
    generateHtmlReport = true
    generateJsonReport = true

    copyHtmlReportToAssets = false
    copyJsonReportToAssets = false
}
