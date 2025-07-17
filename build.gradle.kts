plugins {
    id("abacusflow-base")
    id("com.star-zero.gradle.githook") version "1.2.1"
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

tasks.register("installGitHooks") {
    group = "git"
    description = "安装 Git hook 脚本到 .git/hooks 目录"

    doLast {
        val script = file(".githook/pre-push-check-format.sh")
        val target = file(".git/hooks/pre-push")

        target.writeText(script.readText())
        target.setExecutable(true)

        println("✅ 已安装 pre-push hook 脚本")
    }
}

tasks.named("build") {
    dependsOn(":abacusflow-webapp:buildFrontend")
}

tasks.named("clean") {
    doLast {
        delete("abacusflow-webapp/src/core/openapi")
    }
}
