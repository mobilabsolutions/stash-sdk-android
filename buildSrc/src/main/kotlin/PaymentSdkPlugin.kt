import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.KotlinClosure1
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 19-07-2019.
 */
class PaymentSdkPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        project.plugins.all { plugin: Plugin<*>? ->
            when (plugin) {
                is LibraryPlugin -> {
                    project.extensions.getByType<LibraryExtension>().apply {
                        configureAndroidLibraryOptions(project)
                    }
                }
            }
            true
        }
    }

    private fun Project.configureKapt() {
        apply(plugin = "kotlin-kapt")
        configure<KaptExtension> {
            correctErrorTypes = true
            useBuildCache = true
        }
    }

    private fun LibraryExtension.configureAndroidLibraryOptions(project: Project) {
        val getBranch = ("git rev-parse --abbrev-ref HEAD").runCommand(project)
        val getCommitHash = ("git rev-parse --short HEAD").runCommand(project)
        val getCommitCount = ("git rev-list --count HEAD").runCommand(project)

        val sdkVersionCode = project.propOrDefWithTravis(PaymentSdkRelease.travisBuildNumber, getCommitCount).toInt()
        val sdkVersionName = project.propOrDefWithTravis(PaymentSdkRelease.travisTag, "${DemoRelease.versionName}-$getBranch-$getCommitHash")

        compileSdkVersion(PaymentSdkBuildConfigs.compileSdk)
        buildToolsVersion(PaymentSdkBuildConfigs.buildtoolsVersion)
        defaultConfig.apply {
            minSdkVersion(PaymentSdkBuildConfigs.minSdk)
            targetSdkVersion(PaymentSdkBuildConfigs.targetSdk)
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
                buildConfigField("String", "mobilabBackendUrl", "\"" + project.propOrDefWithTravis(PaymentSdkRelease.mobilabBackendUrl, "") + "\"")
                buildConfigField("String", "testPublishableKey", "\"" + project.propOrDefWithTravis(PaymentSdkRelease.testPublishableKey, "") + "\"")
            }
            getByName("release") {
                isMinifyEnabled = false
                buildConfigField("String", "mobilabBackendUrl", "\"" + project.propOrDefWithTravis(PaymentSdkRelease.mobilabBackendUrl, "") + "\"")
                buildConfigField("String", "testPublishableKey", "\"" + project.propOrDefWithTravis(PaymentSdkRelease.testPublishableKey, "") + "\"")
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
    }
}