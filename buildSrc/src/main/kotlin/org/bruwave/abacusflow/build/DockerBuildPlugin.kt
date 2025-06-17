//package org.bruwave.abacusflow.build
//
//import org.gradle.api.Plugin
//import org.gradle.api.Project
//import org.gradle.api.file.Directory
//import org.gradle.api.file.RegularFile
//import org.gradle.api.provider.Provider
//import org.gradle.api.tasks.Copy
//import org.gradle.api.tasks.Exec
//import org.gradle.kotlin.dsl.register
//
//class DockerBuildPlugin : Plugin<Project> {
//    override fun apply(project: Project): Unit = with(project) {
//        val jarName = "${project.name}-${version}.jar"
//        val jarPath: Provider<RegularFile?> = layout.buildDirectory.file("libs/$jarName")
//        val dockerAppDir: Directory = layout.projectDirectory.dir("docker/app")
//
//        tasks.register<Copy>("prepareDockerJar") {
//            group = "docker"
//            description = "Copy JAR to docker/app folder"
//            from(jarPath.map { it.asFile })
//            into(dockerAppDir)
//            rename { _ -> jarName }
//        }
//
//        tasks.register<Exec>("buildDockerImage") {
//            group = "docker"
//            description = "Build Docker image for ${project.name}"
//            dependsOn("build", "prepareDockerJar")
//
//            workingDir = file("docker")
//            commandLine = listOf(
//                "docker", "build",
//                "-f", "Dockerfile",
//                "-t", "${project.name}:${project.version}",
//                "."
//            )
//        }
//    }
//}
