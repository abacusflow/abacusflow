plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
//    implementation("org.gradle.kotlin:gradle-kotlin-dsl-provider:3.6.1")
//    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}