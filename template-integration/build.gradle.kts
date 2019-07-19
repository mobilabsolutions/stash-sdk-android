/*
 * Copyright © MobiLab Solutions GmbH
 */

plugins {
    id("com.android.library")
    id("PaymentSdkPlugin")
}

dependencies {
    implementation(project(Modules.paymentSdk))

    testImplementation(Libs.junit)
    kaptTest(Libs.Dagger.compiler)

    androidTestImplementation(Libs.AndroidX.Test.runner)
    androidTestImplementation(Libs.AndroidX.Test.espressoCore)
    kaptAndroidTest(Libs.Dagger.compiler)
}
