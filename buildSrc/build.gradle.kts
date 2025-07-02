plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.kotlin:kotlin-allopen:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.kotlin:kotlin-noarg:${libs.versions.kotlin.get()}")
//    implementation("com.github.docker-java:docker-java-core:${libs.versions.docker.java.get()}")

    implementation("org.springframework.boot:spring-boot-gradle-plugin:${libs.versions.spring.boot.get()}")
//    implementation("io.spring.gradle:dependency-management-plugin:1.1.4")

    implementation("org.jlleitschuh.gradle:ktlint-gradle:${libs.versions.ktlint.get()}")

    testImplementation(kotlin("test"))
}

//kotlin {
//    jvmToolchain(17)
//}