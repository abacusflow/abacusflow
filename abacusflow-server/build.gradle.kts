plugins {
    id("abacusflow-base")
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":abacusflow-usecase"))
    implementation(project(":abacusflow-protal:abacusflow-protal-web"))
}
