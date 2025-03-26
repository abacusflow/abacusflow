plugins {
    `kotlin-dsl`
}




repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
//    implementation("org.jetbrains.kotlin:kotlin-noarg:${libs.versions.kotlin.get()}")
//    implementation("org.jetbrains.kotlin:kotlin-allopen:${libs.versions.kotlin.get()}")
    testImplementation(kotlin("test"))
}

//checkstyle {
//    config = resources.text.fromFile("checkstyle.xml", "UTF-8")
//    isShowViolations = true
//    isIgnoreFailures = false
//}

kotlin {
    jvmToolchain(17)
}