@file:Suppress("UnstableApiUsage")

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.plugins.signing.SigningExtension

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 29-07-2019.
 */
object MavenPublish {
    fun Project.configureMavenPublish(stashExtension: StashExtension) {
        apply(plugin = "maven-publish")
        configure<PublishingExtension> {
            publications {
                println("name: ${stashExtension.name}")
                println("name: ${stashExtension.versionName}")
                println("name: ${stashExtension.versionCode}")
                if (stashExtension.name == "template") {
                    return@publications
                }

                create<MavenPublication>(stashExtension.name.toLowerCase()) {
                    groupId = "com.mobilabsolutions.stash"
                    artifactId = stashExtension.name.toLowerCase()
                    version = stashExtension.versionName

                    artifact("$buildDir/outputs/aar/${stashExtension.name}-release.aar")
                    artifact(tasks[Dokka.JAVADOC_TASK])
                    artifact(tasks[Dokka.SOURCES_JAR_TASK])

                    versionMapping {
                        usage("java-api") {
                            fromResolutionOf("runtimeClasspath")
                        }
                        usage("java-runtime") {
                            fromResolutionResult()
                        }
                    }

                    pom {
                        name.set("Stash - ${stashExtension.name}")
                        description.set(stashExtension.description)
                        url.set("https://mobilabsolutions.com/")
                        licenses {
                            license {
                                name.set("The Apache License, Version 2.0")
                                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }
                        developers {
                            developer {
                                id.set("Ugi")
                                name.set("Uglješa Jovanović")
                                email.set("ugi@mobilabsolutions.com")
                            }
                            developer {
                                id.set("Yisuk")
                                name.set("Yisuk Kim")
                                email.set("yisuk@mobilabsolutions.com")
                            }
                            developer {
                                id.set("Biju")
                                name.set("Biju Parvathy")
                                email.set("biju@mobilabsolutions.com")
                            }
                        }
                        scm {
                            connection.set("scm:git:https://github.com/mobilabsolutions/payment-sdk-android-open.git")
                            developerConnection.set("scm:git:https://github.com/mobilabsolutions/payment-sdk-android-open.git")
                            url.set("https://github.com/mobilabsolutions/payment-sdk-android-open")
                        }
                        withXml {
                            val dependenciesNode = asNode().appendNode("dependencies")
                            // List all "implementation" dependencies (for new Gradle) as "runtime" dependencies
                            val config = project.configurations.findByName("implementation")
                            config?.dependencies?.forEach { dep ->
                                if (dep.group?.equals("payment-sdk-android-open") == true) {
                                    with(dependenciesNode.appendNode("dependency")) {
                                        appendNode("groupId", "com.mobilabsolutions.stash")
                                        appendNode("artifactId", "core")
                                        appendNode("version", stashExtension.versionName)
                                        appendNode("scope", "runtime")
                                    }
                                } else if (dep.group != null && dep.name != "unspecified" && dep.version != null) {
                                    with(dependenciesNode.appendNode("dependency")) {
                                        appendNode("groupId", dep.group)
                                        appendNode("artifactId", dep.name)
                                        appendNode("version", dep.version)
                                        appendNode("scope", "runtime")
                                    }
                                }
                            }

                            val apiConfig = project.configurations.findByName("api")
                            apiConfig?.dependencies?.forEach {
                                if (it.group != null && it.name != "unspecified" && it.version != null) {
                                    with(dependenciesNode.appendNode("dependency")) {
                                        appendNode("groupId", it.group)
                                        appendNode("artifactId", it.name)
                                        appendNode("version", it.version)
                                        appendNode("scope", "compile")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            repositories {
                maven {
                    name = "nexus"

                    val releasesRepoUrl = "https://nexus.mblb.net/repository/releases/"
                    val snapshotsRepoUrl = "https://nexus.mblb.net/repository/snapshots/"

                    // It's a release if tagged, else snapshot
                    url = uri(if (isTravisTag) releasesRepoUrl else snapshotsRepoUrl)

                    credentials {
                        username = propOrDefWithTravis(StashRelease.MobilabNexusUsername, "")
                        password = propOrDefWithTravis(StashRelease.MobilabNexusPassword, "")
                    }
                }
            }
            apply(plugin = "signing")
            configure<SigningExtension> {
                sign(publications[stashExtension.name.toLowerCase()])
            }
        }
    }
}