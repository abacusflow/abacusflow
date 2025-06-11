plugins {
    id("invenflow-base")
}

dependencies {
    implementation(project(":invenflow-usecase:invenflow-usecase-commons"))
    implementation(project(":invenflow-core:invenflow-user"))
}
