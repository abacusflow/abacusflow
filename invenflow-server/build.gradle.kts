plugins {
    id("invenflow-base")
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":invenflow-usecase"))
    implementation(project(":invenflow-protal:invenflow-protal-web"))
}
