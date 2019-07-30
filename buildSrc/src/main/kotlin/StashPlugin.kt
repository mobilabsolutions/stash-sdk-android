
import Dokka.configureAndroidProjectForDokka
import MavenPublish.configureMavenPublish
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.jaredsburrows.license.LicenseReportExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.KotlinClosure1
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 19-07-2019.
 */
@Suppress("unused")
class StashPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val stashExtension =
            project.extensions.create("stash", StashExtension::class.java, project)

        project.configureKotlin()
        project.plugins.all { plugin: Plugin<*>? ->
            when (plugin) {
                is LibraryPlugin -> {
                    project.extensions.getByType<LibraryExtension>().apply {
                        configureAndroidLibraryOptions(project, stashExtension)

                    }
                }
            }
            true
        }
        project.configureLicenseReport()
        project.configureCommonDependencies()
    }

    private fun Project.configureKotlin() {
        apply(plugin = "kotlin-android")
        apply(plugin = "kotlin-kapt")
        configure<KaptExtension> {
            correctErrorTypes = true
            useBuildCache = true
        }
        apply(plugin = "kotlin-android-extensions")
        configure<AndroidExtensionsExtension> {
            isExperimental = true
        }
    }

    private fun LibraryExtension.configureAndroidLibraryOptions(
        project: Project,
        stashExtension: StashExtension
    ) {
        val getBranch = ("git rev-parse --abbrev-ref HEAD").runCommand(project)
        val getCommitHash = ("git rev-parse --short HEAD").runCommand(project)
        val getCommitCount = ("git rev-list --count HEAD").runCommand(project)

        val sdkVersionCode = project.propOrDefWithTravis(StashRelease.travisBuildNumber, getCommitCount).toInt()
        val sdkVersionName =
            project.propOrDefWithTravis(StashRelease.travisTag, "${DemoRelease.versionName}-$getBranch-$getCommitHash")

        stashExtension.versionCode = sdkVersionCode
        stashExtension.versionName = sdkVersionName

        compileSdkVersion(StashBuildConfigs.compileSdk)
        buildToolsVersion(StashBuildConfigs.buildtoolsVersion)
        defaultConfig.apply {
            minSdkVersion(StashBuildConfigs.minSdk)
            targetSdkVersion(StashBuildConfigs.targetSdk)
            versionName = if (isTravisTag) sdkVersionName else "$sdkVersionName-SNAPSHOT"
            versionCode = sdkVersionCode
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        buildTypes {
            getByName("debug") {
                resValue(
                    "string",
                    "template_public_key",
                    "\"" + project.propOrDefWithTravis(StashRelease.templatePublishableKey, "") + "\""
                )
                buildConfigField(
                    "String",
                    "newBsApiUrl",
                    "\"" + project.propOrDefWithTravis(StashRelease.newBsApiUrl, "") + "\""
                )
                buildConfigField(
                    "String",
                    "mobilabBackendUrl",
                    "\"" + project.propOrDefWithTravis(StashRelease.mobilabBackendUrl, "") + "\""
                )
                buildConfigField(
                    "String",
                    "testPublishableKey",
                    "\"" + project.propOrDefWithTravis(StashRelease.testPublishableKey, "") + "\""
                )
            }
            getByName("release") {
                isMinifyEnabled = false
                buildConfigField(
                    "String",
                    "newBsApiUrl",
                    "\"" + project.propOrDefWithTravis(StashRelease.newBsApiUrl, "") + "\""
                )
                buildConfigField(
                    "String",
                    "mobilabBackendUrl",
                    "\"" + project.propOrDefWithTravis(StashRelease.mobilabBackendUrl, "") + "\""
                )
                buildConfigField(
                    "String",
                    "testPublishableKey",
                    "\"" + project.propOrDefWithTravis(StashRelease.testPublishableKey, "") + "\""
                )
            }
        }

        lintOptions {
            isAbortOnError = false
        }

        testOptions {
            unitTests.apply {
                isIncludeAndroidResources = true
                all(KotlinClosure1<Any, Test>({
                    (this as Test).also {
                        maxHeapSize = "1024m"
                        testLogging {
                            events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED)
                        }
                    }
                }, this))
            }
        }
        project.configureAndroidProjectForDokka()
        project.configureMavenPublish(stashExtension)
    }

    private fun Project.configureLicenseReport() {
        configure<LicenseReportExtension> {
            generateHtmlReport = true
            generateJsonReport = true

            copyHtmlReportToAssets = false
            copyJsonReportToAssets = false
        }
    }

    private fun Project.configureCommonDependencies() {
        dependencies {
            "implementation"(Libs.Kotlin.stdlib)
            "implementation"(Libs.Dagger.dagger)
            "kapt"(Libs.Dagger.compiler)
            "implementation"(Libs.AndroidX.constraintlayout)
            "implementation"(Libs.AndroidX.appcompat)
            "implementation"(Libs.Google.material)
        }
    }
}