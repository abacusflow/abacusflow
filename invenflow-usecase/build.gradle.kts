plugins {
    id("invenflow-base")
}

dependencies {
    api(project("invenflow-usecase-commons"))
    api(project("invenflow-usecase-user"))
    implementation(libs.postgresql)
    implementation(libs.spring.boot.starter.data.jpa)
}
