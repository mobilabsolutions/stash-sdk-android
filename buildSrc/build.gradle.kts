/*
 * Copyright © MobiLab Solutions GmbH
 */


plugins {
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    jcenter()
    google()
}

dependencies {
    // this needs to be improved by using the same variables by the root project.
    implementation("com.android.tools.build:gradle:3.4.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41")
    implementation("com.jaredsburrows:gradle-license-plugin:0.8.5")
    implementation("org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.18")
}

gradlePlugin {
    plugins {
        register("StashPlugin") {
            id = "StashPlugin"
            implementationClass = "StashPlugin"
        }
    }
}