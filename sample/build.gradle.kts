/*
 * Copyright © MobiLab Solutions GmbH
 */

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

val haveFabricApiKey = propOrDefWithTravis(DemoRelease.fabricApiKey, "").isNotEmpty()

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
    id("com.github.ben-manes.versions")
    id("androidx.navigation.safeargs.kotlin")
}
if (haveFabricApiKey) {
    apply(plugin = "io.fabric")
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
}

androidExtensions {
    isExperimental = true
}

android {
    compileSdkVersion(StashBuildConfigs.compileSdk)
    buildToolsVersion(StashBuildConfigs.buildtoolsVersion)
    defaultConfig {
        applicationId = "com.mobilabsolutions.stash.sample"
        minSdkVersion(StashBuildConfigs.minSdk)
        targetSdkVersion(StashBuildConfigs.targetSdk)
        versionCode = propOrDefWithTravis(StashRelease.travisBuildNumber, DemoRelease.versionCode).toInt()
        versionName = "${DemoRelease.versionName}-$versionCode"
        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments = mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }

        manifestPlaceholders = mapOf("fabric-api-key" to propOrDefWithTravis(DemoRelease.fabricApiKey, ""))
    }

    signingConfigs {
        getByName("debug") {
            storeFile = rootProject.file("signing/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "mobilabBackendUrl", "\"" + propOrDefWithTravis(StashRelease.mobilabBackendUrl, "") + "\"")
            buildConfigField("String", "newBsApiKey", "\"" + propOrDefWithTravis(StashRelease.testPublishableKey, "") + "\"")

            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")
            ext.set("alwaysUpdateBuildId", false)
            isCrunchPngs = false
            splits {
                density.isEnable = false
                abi.isEnable = false
            }
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")

            buildConfigField("String", "mobilabBackendUrl", "\"" + propOrDefWithTravis(StashRelease.mobilabBackendUrl, "") + "\"")
            buildConfigField("String", "newBsApiKey", "\"" + propOrDefWithTravis(StashRelease.testPublishableKey, "") + "\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    dataBinding {
        isEnabled = true
    }

    dexOptions {
        preDexLibraries = !isTravisBuild
    }

    lintOptions {
        isAbortOnError = false
    }
    packagingOptions {
        pickFirst("META-INF/atomicfu.kotlin_module")
    }

}

dependencies {
    testImplementation(Libs.junit)

    androidTestImplementation(Libs.AndroidX.appcompat)
    androidTestImplementation(Libs.AndroidX.constraintlayout)

    androidTestImplementation(Libs.AndroidX.Test.runner)
    androidTestImplementation(Libs.AndroidX.Test.espressoCore)
    androidTestImplementation(Libs.AndroidX.Test.espressoIntents)
    androidTestImplementation(Libs.AndroidX.Test.rules)
    androidTestImplementation(Libs.AndroidX.Test.uiAutomator)

    implementation(project(Modules.stash)) //Core
    implementation(project(Modules.adyenIntegration))
    implementation(project(Modules.bsPayoneIntegration))
    implementation(project(Modules.braintreeIntegration))

    implementation(Libs.Kotlin.stdlib)

    implementation(Libs.AndroidX.constraintlayout)
    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.cardview)

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

    implementation(Libs.AndroidX.Lifecycle.extensions)
    implementation(Libs.AndroidX.Lifecycle.reactivestreams)
    kapt(Libs.AndroidX.Lifecycle.compiler)

    implementation(Libs.Coroutines.core)
    implementation(Libs.Coroutines.rx2)
    implementation(Libs.Coroutines.android)

    compileOnly(Libs.AssistedInject.annotationDagger2)
    kapt(Libs.AssistedInject.processorDagger2)

    implementation(Libs.mvrx)

    implementation(Libs.Epoxy.epoxy)
    implementation(Libs.Epoxy.dataBinding)
    kapt(Libs.Epoxy.processor)

    implementation(Libs.AndroidX.Navigation.fragment)
    implementation(Libs.AndroidX.Navigation.ui)

    implementation(Libs.AndroidX.Room.common)
    implementation(Libs.AndroidX.Room.runtime)
    implementation(Libs.AndroidX.Room.rxjava2)
    implementation(Libs.AndroidX.Room.ktx)
    kapt(Libs.AndroidX.Room.compiler)

    implementation(Libs.Epoxy.epoxy)
    implementation(Libs.Epoxy.dataBinding)
    kapt(Libs.Epoxy.processor)

    implementation(Libs.Google.crashlytics) {
        isTransitive = true
    }


}

tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    resolutionStrategy {
        componentSelection {
            all {
                val rejected = listOf("alpha", "beta", "rc", "cr", "m", "preview")
                        .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-]*") }
                        .any { it.matches(candidate.version) }
                if (rejected) {
                    reject("Release candidate")
                }
            }
        }
    }
    // optional parameters
    checkForGradleUpdate = true
    outputFormatter = "json"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"
}

licenseReport {
    generateHtmlReport = true
    generateJsonReport = true

    copyHtmlReportToAssets = false
    copyJsonReportToAssets = false
}