plugins {
    id("invenflow-base")
}

dependencies {
    implementation(project(":invenflow-usecase"))
    implementation(project(":invenflow-protal:invenflow-protal-web"))
//    implementation(libs.spring.boot.starter.web)
}