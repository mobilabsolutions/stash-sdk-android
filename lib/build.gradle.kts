/*
 * Copyright © MobiLab Solutions GmbH
 */

import java.io.ByteArrayOutputStream
import org.jetbrains.dokka.gradle.DokkaAndroidTask

plugins {
    id("com.android.library")
    id("org.jetbrains.dokka-android")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
}

androidExtensions {
    isExperimental = true
}


fun String.runCommand() : String {
    val command = this
    val output = ByteArrayOutputStream()
    project.exec {
        this.workingDir = project.rootDir
        this.commandLine = command.split(" ")
        this.standardOutput = output
    }
    return String(output.toByteArray()).trim()
}

val getBranch = ("git rev-parse --abbrev-ref HEAD").runCommand()

val getCommitHash = ("git rev-parse --short HEAD").runCommand()


val getCommitCount = ("git rev-list --count HEAD").runCommand()


val sdkVersionCode = getCommitCount
val sdkVersionName = "${getBranch}-${getCommitHash}"

android {
    compileSdkVersion(PaymentSdkBuildConfigs.compileSdk)
    buildToolsVersion(PaymentSdkBuildConfigs.buildtoolsVersion)

    defaultConfig {
        minSdkVersion(PaymentSdkBuildConfigs.minSdk)
        targetSdkVersion(PaymentSdkBuildConfigs.targetSdk)


        versionCode = propOrDefWithTravis(PaymentSdkRelease.travisBuildNumber, sdkVersionCode).toInt()
        versionName = propOrDefWithTravis(PaymentSdkRelease.travisTag, sdkVersionName)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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
    implementation(Libs.Kotlin.stdlib)

    implementation(Libs.AndroidX.constraintlayout)
    implementation(Libs.AndroidX.appcompat)

    api(Libs.OkHttp.okhttp)
    api(Libs.OkHttp.loggingInterceptor)

    api(Libs.Retrofit.retrofit)
    api(Libs.Retrofit.retrofit_rxjava_adapter)
    api(Libs.Retrofit.gsonConverter)

    api(Libs.timber)

    api(Libs.RxJava.rxJava)
    api(Libs.RxJava.rxAndroid)
    api(Libs.RxJava.rxKotlin)

    api(Libs.threetenabp)

    implementation(Libs.AndroidX.recyclerview)
    implementation(Libs.Google.material)

    implementation(Libs.Utils.commonsValidator)


    implementation(Libs.Dagger.dagger)
    implementation(Libs.Dagger.daggerAndroid)
    implementation(Libs.Dagger.androidSupport)
    kapt(Libs.Dagger.compiler)


    implementation(Libs.caligraphy)
    implementation(Libs.viewPump)

    api(Libs.threetenabp)
    implementation(Libs.iban4j)

    testImplementation(project(Modules.bsPayoneIntegration))
    testImplementation(project(Modules.adyenIntegration))
    testImplementation(project(Modules.braintreeIntegration))

    testImplementation(Libs.Kotlin.test)

    testImplementation(Libs.junit)
    testImplementation(Libs.mockitoCore)
    testImplementation(Libs.mockwebserver)
    testImplementation(Libs.PowerMock.module)
    testImplementation(Libs.PowerMock.api)
    kaptTest(Libs.Dagger.compiler)

    androidTestImplementation(Libs.mockwebserver)
    androidTestImplementation(Libs.AndroidX.Test.runner)
    androidTestImplementation(Libs.AndroidX.Test.core)
    androidTestImplementation(Libs.AndroidX.Test.coreKtx)
    androidTestImplementation(Libs.AndroidX.Test.ext)
    androidTestImplementation(Libs.AndroidX.Test.espressoCore)
    kaptAndroidTest(Libs.Dagger.compiler)

}


tasks {

    create<DokkaAndroidTask>("dokkaPublic") {
        moduleName = "lib"
        outputFormat = "html"
        outputDirectory = "$buildDir/dokkaPublic"
        packageOptions {
            prefix = "com.mobilabsolutions.payment.android.psdk.internal"
            suppress = true
        }
        includes = listOf(
            "src/main/java/com/mobilabsolutions/payment/android/psdk/model/model-package-description.md",
            "src/main/java/com/mobilabsolutions/payment/android/psdk/payment-sdk-package-description.md"

        )
    }
    dokka {
        moduleName = "lib"
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
    }
}

configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(0, TimeUnit.SECONDS)
        cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
    }
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



