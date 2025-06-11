plugins {
    id("abacusflow-base")
    id("abacusflow-jpa")
}

dependencies {
    implementation(project(":abacusflow-infra:abacusflow-commons"))
    implementation(project(":abacusflow-core:abacusflow-user"))
    api(libs.spring.data.jpa)
}
