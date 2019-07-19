/*
 * Copyright © MobiLab Solutions GmbH
 */

plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    google()
}

dependencies {
    implementation("com.android.tools.build:gradle:3.4.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.40")
}

gradlePlugin {
    plugins {
        register("PaymentSdkPlugin") {
            id = "PaymentSdkPlugin"
            implementationClass = "PaymentSdkPlugin"
        }
    }
}