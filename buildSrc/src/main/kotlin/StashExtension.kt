import org.gradle.api.Project

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 24-07-2019.
 */
open class StashExtension(
    val project: Project
) {
    var name: String = project.name
    var description: String? = null
    var versionName: String = ""
    var versionCode: Int = 0

}