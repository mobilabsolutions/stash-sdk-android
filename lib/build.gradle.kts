/*
 * Copyright © MobiLab Solutions GmbH
 */

import org.jetbrains.dokka.gradle.DokkaAndroidTask
import java.io.ByteArrayOutputStream

plugins {
    id("com.android.library")
    id("org.jetbrains.dokka-android")
    id("maven-publish")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
    signing
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
}

androidExtensions {
    isExperimental = true
}

fun String.runCommand(): String {
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

val sdkVersionCode = propOrDefWithTravis(PaymentSdkRelease.travisBuildNumber, getCommitCount).toInt()

val sdkVersionName = propOrDefWithTravis(PaymentSdkRelease.travisTag, "$getBranch-$getCommitHash")

android {
    compileSdkVersion(PaymentSdkBuildConfigs.compileSdk)
    buildToolsVersion(PaymentSdkBuildConfigs.buildtoolsVersion)

    defaultConfig {
        minSdkVersion(PaymentSdkBuildConfigs.minSdk)
        targetSdkVersion(PaymentSdkBuildConfigs.targetSdk)

        versionCode = sdkVersionCode
        versionName = if (isTravisTag) {
            sdkVersionName
        } else {
            "$sdkVersionName-SNAPSHOT"
        }

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

licenseReport {
    generateHtmlReport = true
    generateJsonReport = true

    copyHtmlReportToAssets = false
    copyJsonReportToAssets = false
}

configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(0, TimeUnit.SECONDS)
        cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
    }
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

    val dokkaJavadoc = create<DokkaAndroidTask>("dokkaJavadoc") {
        moduleName = "lib"
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/dokkaJavadoc"
    }

    create<Jar>("javadocJar") {
        dependsOn(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        from("$buildDir/dokkaJavadoc")
    }

    create<Jar>("sourcesJar") {
        from(android.sourceSets["main"].java.srcDirs)
        archiveClassifier.set("sources")
    }

    publish {
        dependsOn(build)
    }

    publishToMavenLocal {
        dependsOn(build)
    }
}

publishing {
    publications {
        create<MavenPublication>("lib") {
            groupId = "com.mobilabsolutions.payment.android.psdk"
            artifactId = "payment-sdk-lib"
            version = android.defaultConfig.versionName

            artifact("$buildDir/outputs/aar/lib-release.aar")

            artifact(tasks["javadocJar"])

            artifact(tasks["sourcesJar"])

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name.set("Android Payment SDK")
                description.set("The payment SDK simplifies the integration of payments into our applications and abstracts away a lot of the internal complexity that different payment service providers' solutions have.")
                url.set("https://mobilabsolutions.com/")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("Ugi")
                        name.set("Uglješa Jovanović")
                        email.set("ugi@mobilabsolutions.com")
                    }
                    developer {
                        id.set("Yisuk")
                        name.set("Yisuk Kim")
                        email.set("yisuk@mobilabsolutions.com")
                    }
                    developer {
                        id.set("Biju")
                        name.set("Biju Parvathy")
                        email.set("Biju@mobilabsolutions.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/mobilabsolutions/payment-sdk-android-open.git")
                    developerConnection.set("scm:git:https://github.com/mobilabsolutions/payment-sdk-android-open.git")
                    url.set("https://github.com/mobilabsolutions/payment-sdk-android-open")
                }
                withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")
                    // List all "implementation" dependencies (for new Gradle) as "runtime" dependencies
                    configurations.implementation.get().allDependencies.forEach {
                        if (it.group != null && it.name != "unspecified" && it.version != null) {
                            with(dependenciesNode.appendNode("dependency")) {
                                appendNode("groupId", it.group)
                                appendNode("artifactId", it.name)
                                appendNode("version", it.version)
                                appendNode("scope", "runtime")
                            }
                        }
                    }
                    // List all "api" dependencies as "compile" dependencies
                    configurations.api.get().allDependencies.forEach {
                        if (it.group != null && it.name != "unspecified" && it.version != null) {
                            with(dependenciesNode.appendNode("dependency")) {
                                appendNode("groupId", it.group)
                                appendNode("artifactId", it.name)
                                appendNode("version", it.version)
                                appendNode("scope", "compile")
                            }
                        }
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "Nexus"
            url = uri("https://nexus.mblb.net/repository/mblb-internal/")
            credentials {
                username = propOrDefWithTravis(PaymentSdkRelease.MobilabNexusUsername, "")
                password = propOrDefWithTravis(PaymentSdkRelease.MobilabNexusPassword, "")
            }
        }
    }
}

signing {
    sign(publishing.publications["lib"])
}
