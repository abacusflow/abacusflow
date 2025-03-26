pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal() // 必须包含插件仓库
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal() // 必须包含插件仓库
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
