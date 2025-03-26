pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

// 生成libs静态类插件
//plugins {
//    id("dev.panuszewski.typesafe-conventions") version "0.6.0-RC1"
//}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
