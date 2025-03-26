import org.gradle.buildinit.plugins.internal.VersionCatalogDependencyRegistry

//val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    kotlin("jvm")
}
dependencies {
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