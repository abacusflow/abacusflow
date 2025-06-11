plugins {
    id("invenflow-base")
    id("invenflow-jpa")
}
dependencies {
    implementation(project(":invenflow-infra:invenflow-commons"))
}
