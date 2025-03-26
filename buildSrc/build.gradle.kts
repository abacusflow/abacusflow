plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.kotlin:kotlin-allopen:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.kotlin:kotlin-noarg:${libs.versions.kotlin.get()}")

    implementation("org.springframework.boot:spring-boot-gradle-plugin:${libs.versions.spring.boot.get()}")
//    implementation("io.spring.gradle:dependency-management-plugin:1.1.4")

    implementation("org.jlleitschuh.gradle:ktlint-gradle:${libs.versions.ktlint.get()}")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}