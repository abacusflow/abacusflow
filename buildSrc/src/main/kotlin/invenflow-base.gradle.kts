import gradle.kotlin.dsl.accessors._2ae1f5f4c3028690945a5ad212af1642.implementation
import gradle.kotlin.dsl.accessors._2ae1f5f4c3028690945a5ad212af1642.test
import gradle.kotlin.dsl.accessors._2ae1f5f4c3028690945a5ad212af1642.testImplementation
val libsFun = versionCatalogs.named("libs")
plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.jlleitschuh.gradle.ktlint")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(libsFun.findLibrary("jackson.databind").orElseThrow(::AssertionError))
    implementation(libsFun.findLibrary("jackson.kotlin").orElseThrow(::AssertionError))
    implementation(libsFun.findLibrary("jackson.datatype.jsr310").orElseThrow(::AssertionError))
    implementation(libsFun.findLibrary("jackson.datatype.jdk8").orElseThrow(::AssertionError))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}