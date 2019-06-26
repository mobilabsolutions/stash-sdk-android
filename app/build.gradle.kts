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

val testAar = false

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


    implementation(fileTree(mapOf("dir" to "commonLibs", "include" to listOf("*.jar", "*.aar"))))
    if (testAar) {
        implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

        //AAR dependencies (some are duplicates, shouldn't matter as this is only temporary)
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.30")
        implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta2")
        implementation("androidx.appcompat:appcompat:1.0.2")
        implementation("com.squareup.retrofit2:retrofit:2.6.0")
        implementation("com.squareup.retrofit2:adapter-rxjava2:2.6.0")
        implementation("com.squareup.retrofit2:converter-gson:2.6.0")
        implementation("com.squareup.okhttp3:okhttp:4.0.0-alpha02")
        implementation("com.squareup.okhttp3:logging-interceptor:4.0.0-alpha02")
        implementation("com.jakewharton.threetenabp:threetenabp:1.2.0")
        implementation("io.reactivex.rxjava2:rxjava:2.2.9")
        implementation("io.reactivex.rxjava2:rxkotlin:2.3.0")
        implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
        implementation("com.jakewharton.threetenabp:threetenabp:1.2.0")
        implementation("androidx.recyclerview:recyclerview:1.0.0")
        implementation("com.google.android.material:material:1.1.0-alpha05")
        implementation("commons-validator:commons-validator:1.6")
        implementation("com.google.dagger:dagger:2.23.1")
        implementation("com.google.dagger:dagger-android:2.23.1")
        implementation("com.google.dagger:dagger-android-support:2.23.1")
        implementation("io.github.inflationx:calligraphy3:3.1.1")
        implementation("io.github.inflationx:viewpump:2.0.3")
        implementation("org.iban4j:iban4j:3.2.1")


        implementation("com.adyen.checkout:base:2.4.5")
        implementation("com.adyen.checkout:ui:2.4.5")
        implementation("com.adyen.checkout:core:2.4.5")
        implementation("com.adyen.checkout:core-card:2.4.5")

        implementation("com.braintreepayments.api:braintree:3.0.0")
    } else {
        implementation(project(Modules.paymentSdk)) //Core
        implementation(project(Modules.bsPayoneIntegration))
        implementation(project(Modules.braintreeIntegration))
        implementation(project(Modules.adyenIntegration))
    }



    implementation(Libs.threetenabp)

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
