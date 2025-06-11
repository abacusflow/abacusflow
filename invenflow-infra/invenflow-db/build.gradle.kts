plugins {
    id("invenflow-base")
    id("invenflow-jpa")
}

dependencies {
    implementation(project(":invenflow-infra:invenflow-commons"))
    implementation(project(":invenflow-core:invenflow-user"))
    api(libs.spring.data.jpa)
}
