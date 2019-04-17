buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(Libs.androidGradlePlugin)
        classpath(Libs.Kotlin.gradlePlugin)
        classpath(Libs.Kotlin.extensions)
        classpath(Libs.gradleVersionsPlugin)
        classpath(Libs.AndroidX.Navigation.safeArgs)
    }
}

plugins {
    id("com.diffplug.gradle.spotless") version ("3.21.1")
    id("com.github.ben-manes.versions") version ("0.21.0")
}

allprojects {
    repositories {
        google()
        jcenter()
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