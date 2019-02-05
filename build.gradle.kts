buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(Libs.androidGradlePlugin)
        classpath(Libs.Kotlin.gradlePlugin)
        classpath(Libs.Kotlin.extensions)
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