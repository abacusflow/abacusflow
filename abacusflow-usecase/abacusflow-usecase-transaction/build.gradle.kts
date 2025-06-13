plugins {
    id("abacusflow-base")
}

dependencies {
    implementation(project(":abacusflow-usecase:abacusflow-usecase-commons"))
    implementation(project(":abacusflow-core:abacusflow-transaction"))
}
