plugins {
    id("abacusflow-base")
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

tasks.named("build") {
//    dependsOn(":abacusflow-webapp:buildFrontend")
}

tasks.named("clean") {
    doLast {
        delete("abacusflow-webapp/src/core/openapi")
    }
}
