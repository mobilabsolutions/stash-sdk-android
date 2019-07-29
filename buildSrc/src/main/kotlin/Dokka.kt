
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.dokka.gradle.DokkaAndroidTask
import java.io.File

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 29-07-2019.
 */

object Dokka {
    const val DOKKA_PUBLIC = "dokkaPublic"
    const val JAVADOC_TASK = "javadocJar"
    const val SOURCES_JAR_TASK = "sourcesJar"


    fun Project.configureAndroidProjectForDokka() {
        apply(plugin = "org.jetbrains.dokka-android")
        createDocsTask(this)
    }

    fun createDocsTask(
        project: Project
    ) {
//        project.apply<DokkaAndroidPlugin>()
        // We don't use the `dokka` task, but it normally appears in `./gradlew tasks`
        // so replace it with a new task that doesn't show up and doesn't do anything
        project.tasks.replace("dokka")
        val docsTask = project.tasks.create(DOKKA_PUBLIC, DokkaAndroidTask::class.java) {
            moduleName = project.name
            outputDirectory = File(project.buildDir, DOKKA_PUBLIC).absolutePath
            description = "Generates Kotlin documentation in $outputDirectory"
            outputFormat = "html"
            packageOptions {
                prefix = "com.mobilabsolutions.stash.internal"
                suppress = true
            }
        }

        project.tasks.create(JAVADOC_TASK, Jar::class.java) {
            dependsOn(docsTask)
            archiveClassifier.set("javadoc")
            from(File(project.buildDir, DOKKA_PUBLIC).absolutePath)
        }

        project.tasks.create(SOURCES_JAR_TASK, Jar::class.java) {
            project.extensions.getByType<BaseExtension>().apply {
                from(sourceSets["main"].java.srcDirs)
                archiveClassifier.set("sources")
            }
        }
    }
}
