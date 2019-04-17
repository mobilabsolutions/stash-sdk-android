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

val clean by tasks.creating(Delete::class) {
    delete(rootProject.buildDir)
}

configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}