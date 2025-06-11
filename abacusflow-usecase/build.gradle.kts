plugins {
    id("abacusflow-base")
}

dependencies {
    api(project("abacusflow-usecase-commons"))
    api(project("abacusflow-usecase-user"))
    implementation(libs.postgresql)
    implementation(libs.spring.boot.starter.data.jpa)
}
