plugins {
    id("com.android.library")
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

android {
    compileSdkVersion(PaymentSdkBuildConfigs.compileSdk)
    buildToolsVersion(PaymentSdkBuildConfigs.buildtoolsVersion)

    defaultConfig {
        minSdkVersion(PaymentSdkBuildConfigs.minSdk)
        targetSdkVersion(PaymentSdkBuildConfigs.targetSdk)


        versionCode = propOrDefWithTravis(PaymentSdkRelease.travisBuildNumber, PaymentSdkBuildConfigs.sdkVersionCode).toInt()
        versionName = propOrDefWithTravis(PaymentSdkRelease.travisTag, PaymentSdkBuildConfigs.sdkVersionName)
        println("Version code $versionCode versionName $versionName")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {

        getByName("debug") {
            buildConfigField("String", "mobilabBackendUrl", "\"" + propOrDefWithTravis(PaymentSdkRelease.mobilabBackendUrl, "") + "\"")

            buildConfigField("String", "newBsApiUrl", "\"" + propOrDefWithTravis(PaymentSdkRelease.newBsApiUrl, "") + "\"")
            buildConfigField("String", "newBsTestKey", "\"" + propOrDefWithTravis(PaymentSdkRelease.newBsTestKey, "") + "\"")

            buildConfigField("String", "hyperchargeTestKey", "\"" + propOrDefWithTravis(PaymentSdkRelease.hyperchargeTestKey, "") + "\"")
            buildConfigField("String", "braintreeSanboxToken", "\"" + propOrDefWithTravis(PaymentSdkRelease.braintreeSandboxToken, "") + "\"")
        }

        getByName("release") {
            buildConfigField("String", "mobilabBackendUrl", "\"" + propOrDefWithTravis(PaymentSdkRelease.mobilabBackendUrl, "") + "\"")
            buildConfigField("String", "oldBsTestKey", "\"" + propOrDefWithTravis(PaymentSdkRelease.oldBsTestKey, "") + "\"")
            buildConfigField("String", "oldBsApiUrl", "\"" + propOrDefWithTravis(PaymentSdkRelease.oldBsApiUrl, "") + "\"")
            buildConfigField("String", "oldBsExistingSepaAlias", "\"" + propOrDefWithTravis(PaymentSdkRelease.oldBsExistingSepaAlias, "") + "\"")
            buildConfigField("String", "oldBsExistingCcAlias", "\"" + propOrDefWithTravis(PaymentSdkRelease.oldBsExistingCcAlias, "") + "\"")

            buildConfigField("String", "newBsApiUrl", "\"" + propOrDefWithTravis(PaymentSdkRelease.newBsApiUrl, "") + "\"")
            buildConfigField("String", "newBsTestKey", "\"" + propOrDefWithTravis(PaymentSdkRelease.newBsTestKey, "") + "\"")

            buildConfigField("String", "hyperchargeTestKey", "\"" + propOrDefWithTravis(PaymentSdkRelease.hyperchargeTestKey, "") + "\"")
            buildConfigField("String", "braintreeSanboxToken", "\"" + propOrDefWithTravis(PaymentSdkRelease.braintreeSandboxToken, "") + "\"")
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

val javadoc by tasks.creating(Javadoc::class) {
    setSource(android.sourceSets.maybeCreate("main").java.srcDirs)
    classpath += project.files(android.bootClasspath.joinToString { File.pathSeparator })
    setDestinationDir(file("../javadoc/"))
    options.overview = "overview.html"
    isFailOnError = false
}

val javadocPublic by tasks.creating(Javadoc::class) {
    val psdkSources: Set<File> = android.sourceSets.getByName("main").java.srcDirs.filter { dir ->
        println(dir.path)
        var foundPsdk = false
        if (dir.path.contains("psdk")) {
            foundPsdk = true
        }
        foundPsdk
    }.map { dir ->
        File(dir.path + "/com/mobilabsolutions/payment/android/psdk")

    }.toList().toSet()

    psdkSources.forEach {
        println(it)
    }
    setSource(psdkSources)
    exclude("**/internal/**")
    classpath += project.files(android.bootClasspath.joinToString { File.pathSeparator })
    setDestinationDir(file("../javadocPublic/"))
    options.overview = "overview.html"
    isFailOnError = false
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

