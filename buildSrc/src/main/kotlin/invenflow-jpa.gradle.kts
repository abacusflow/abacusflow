val libsFun = versionCatalogs.named("libs")

plugins {
    kotlin("jvm")
    kotlin("plugin.jpa")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(libsFun.findLibrary("spring-data-commons").orElseThrow(::AssertionError))
    implementation(libsFun.findLibrary("hibernate").orElseThrow(::AssertionError))
    implementation(libsFun.findLibrary("jakarta-validation").orElseThrow(::AssertionError))
}