/*
 * Copyright © MobiLab Solutions GmbH
 */

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

        classpath(Libs.licencePlugin)

        classpath(Libs.dokkaPlugin)
    }
}

plugins {
    id("com.diffplug.gradle.spotless") version ("3.23.1")
    id("com.github.ben-manes.versions") version ("0.21.0")
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

subprojects {
    apply(plugin = "com.diffplug.gradle.spotless")
    apply(plugin = "com.jaredsburrows.license")
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

tasks {

}