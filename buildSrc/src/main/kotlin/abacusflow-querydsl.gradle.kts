//import gradle.kotlin.dsl.accessors._fa393d92bf243f86e14930a7cb20dbb9.kapt
//
//val libsFun = versionCatalogs.named("libs")
//
//plugins {
//    kotlin("jvm")
//    kotlin("kapt")
//}
//
//repositories {
//    mavenLocal()
//    mavenCentral()
//}
//
//dependencies {
////    implementation ("com.querydsl:querydsl-jpa:5.1.0")
////    kapt ("com.querydsl:querydsl-apt:5.1.0:jpa")
//    implementation(libsFun.findLibrary("querydsl-jpa").orElseThrow(::AssertionError))
////    kapt(libsFun.findLibrary("querydsl-apt").orElseThrow(::AssertionError))
//    kapt("com.querydsl:querydsl-apt:5.1.0:jpa"){
//        artifact {
//            classifier = "jpa"
//        }
//    }
//}
//
//
