/*
 * Copyright © MobiLab Solutions GmbH
 */

plugins {
    id("com.android.library")
    id("StashPlugin")
}

dependencies {
    implementation(project(Modules.stash))
}


dependencies {
    implementation(project(Modules.stash))
    testImplementation(Libs.junit)
    kaptTest(Libs.Dagger.compiler)

    androidTestImplementation(Libs.AndroidX.Test.runner)
    androidTestImplementation(Libs.AndroidX.Test.espressoCore)
    kaptAndroidTest(Libs.Dagger.compiler)
}
