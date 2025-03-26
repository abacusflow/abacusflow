//val catalogsExtension = project.extensions.getByType<VersionCatalogsExtension>()
//val libs = project.extensions.getByType<LibrariesForLibs>()
//val libs = the<LibrariesForLibs>()

plugins {
    kotlin("jvm")

}

//val libs = the<LibrariesForLibs>()

repositories {
    mavenCentral()
}

dependencies {
    implementation(versionCatalogs.named("libs").findLibrary("jackson.databind").orElseThrow(::AssertionError))
//    implementation(libs.jackson.databind)
//    implementation(libs.jackson.datatype.jsr310)
//    implementation(libs.jackson.module.parameter.names)
//    implementation(libs.jackson.datatype.jdk8)
//    implementation(libs.spring.boot.starter)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}