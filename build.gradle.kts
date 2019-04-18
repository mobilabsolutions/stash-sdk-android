buildscript {
    repositories {
        google()
        jcenter()
        maven(url = "https://maven.fabric.io/public")
    }
    dependencies {
        classpath(Libs.androidGradlePlugin)

        classpath(Libs.Kotlin.gradlePlugin)
        classpath(Libs.Kotlin.extensions)

        classpath(Libs.gradleVersionsPlugin)

        classpath(Libs.AndroidX.Navigation.safeArgs)

        classpath(Libs.Google.fabricPlugin)
    }
}

plugins {
    id("com.diffplug.gradle.spotless") version ("3.22.0")
    id("com.github.ben-manes.versions") version ("0.21.0")
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://kotlin.bintray.com/kotlinx/")
        maven(url = "https://nexus.mblb.net/repository/mblb-internal/") {
            credentials {
                username = propOrDefWithTravis(PaymentSdkRelease.mobilabNexusUser, "")
                password = propOrDefWithTravis(PaymentSdkRelease.mobilabNexusPassword, "")
            }
        }
    }
}

subprojects {
    apply(plugin = "com.diffplug.gradle.spotless")
    spotless {
        kotlin {
            target("**/*.kt")
            ktlint("0.31.0")
        }
    }
}

configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}