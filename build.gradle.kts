plugins {
    id("abacusflow-base")
//    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

group = "org.bruwave.abacusflow"
version = libs.versions.abacusflow.get()


tasks.register<Exec>("buildWebsiteDockerImage") {
    group = "build"
    description = "构建官网 Docker 镜像"

    workingDir = file("$projectDir/website")
    commandLine = listOf(
        "docker", "build",
        "-t", "abacusflow-website:latest",
        "."
    )
}