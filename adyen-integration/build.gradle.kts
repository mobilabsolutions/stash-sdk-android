/*
 * Copyright © MobiLab Solutions GmbH
 */

import org.jetbrains.dokka.gradle.DokkaAndroidTask

plugins {
    id("com.android.library")
    id("PaymentSdkPlugin")
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

dependencies {
    implementation(project(Modules.paymentSdk))
    implementation(Libs.Kotlin.stdlib)

    implementation(Libs.Adyen.base)
    implementation(Libs.Adyen.ui)
    implementation(Libs.Adyen.core)
    implementation(Libs.Adyen.coreCard)

    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.constraintlayout)
    implementation(Libs.Google.material)
    implementation(Libs.Dagger.dagger)
    kapt(Libs.Dagger.compiler)

    testImplementation(Libs.junit)
    testImplementation(Libs.robolectric)
    testImplementation(Libs.AndroidX.Test.core)
    kaptTest(Libs.Dagger.compiler)

    androidTestImplementation(project(Modules.paymentSdk))
    androidTestImplementation(Libs.junit)
    androidTestImplementation(Libs.mockitoCore)
    androidTestImplementation(Libs.AndroidX.Test.runner)
    androidTestImplementation(Libs.AndroidX.Test.espressoCore)
    androidTestImplementation(Libs.AndroidX.Test.core)
    kaptAndroidTest(Libs.Dagger.compiler)
}

licenseReport {
    generateHtmlReport = true
    generateJsonReport = true

    copyHtmlReportToAssets = false
    copyJsonReportToAssets = false
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
        create<MavenPublication>("adyen") {
            groupId = "com.mobilabsolutions.payment.android.psdk.integration.adyen"
            artifactId = "payment-sdk-adyen"
            version = android.defaultConfig.versionName

            artifact("$buildDir/outputs/aar/adyen-integration-release.aar")

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
                name.set("Android Payment SDK - Adyen")
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
                        if (it.group == "payment-sdk-android-open") { // Core lib
                            with(dependenciesNode.appendNode("dependency")) {
                                appendNode("groupId", "com.mobilabsolutions.payment.android.psdk")
                                appendNode("artifactId", "payment-sdk-lib")
                                appendNode("version", android.defaultConfig.versionName)
                                appendNode("scope", "compile")
                            }
                        } else if (it.group != null && it.name != "unspecified" && it.version != null) {
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
            name = "nexus"

            val releasesRepoUrl = "https://nexus.mblb.net/repository/releases/"
            val snapshotsRepoUrl = "https://nexus.mblb.net/repository/snapshots/"

            // It's a release if tagged, else snapshot
            url = uri(if (isTravisTag) releasesRepoUrl else snapshotsRepoUrl)

            credentials {
                username = propOrDefWithTravis(PaymentSdkRelease.MobilabNexusUsername, "")
                password = propOrDefWithTravis(PaymentSdkRelease.MobilabNexusPassword, "")
            }
        }
    }
}

signing {
    sign(publishing.publications["adyen"])
}

