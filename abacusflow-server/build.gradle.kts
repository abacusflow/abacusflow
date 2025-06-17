plugins {
    id("abacusflow-base")
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":abacusflow-usecase"))
    implementation(project(":abacusflow-portal:abacusflow-portal-web"))
}

//apply<DockerBuildPlugin>()