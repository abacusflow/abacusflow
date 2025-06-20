import com.github.gradle.node.npm.task.NpmTask

group = "org.bruwave.abacusflow"
version = libs.versions.abacusflow.get()

plugins {
    alias(libs.plugins.node.gradle)
}

node {
    version.set("22.16.0")
    download.set(true)
}

tasks.register<NpmTask>("installDependencies") {
    group = "install"
    description = "安装依赖"
    args.set(listOf("ci"))
    inputs.files("package.json", "package-lock.json")
}

tasks.register<NpmTask>("tsFormat") {
    group = "formatting"
    description = "prettier代码风格统一"
    dependsOn("installDependencies")
    args.set(listOf("run", "format"))
}

tasks.register<NpmTask>("lint-ts") {
    group = "lint"
    description = "eslint代码检查"
    dependsOn("installDependencies", "tsFormat")
    args.set(listOf("run", "lint"))
}

tasks.register<NpmTask>("openapiGenerateTs") {
    group = "openapi tools"
    description = "OpenAPI 客户端代码生成"
    dependsOn("installDependencies")
    args.set(listOf("run", "generate"))
}

tasks.register<NpmTask>("buildFrontend") {
    group = "build"
    description = "前端打包构建"
    dependsOn("installDependencies", "lint-ts", "openapiGenerate")
    args.set(listOf("run", "build"))
    inputs.files(
        "package.json",
        "package-lock.json",
        "angular.json",
        "tsconfig.json",
        "tsconfig.app.json"
    )
    inputs.dir("src")
    outputs.dir("dist")
}

tasks.register<Exec>("buildDockerImage") {
    group = "build"
    description = "构建前端 Docker 镜像"
    dependsOn("buildFrontend") // 保证前端构建先完成

    workingDir = projectDir // Dockerfile 所在目录
    commandLine = listOf(
        "docker", "build",
        "-t", "abacusflow-web:${project.version}",
        "."
    )
}
