import java.io.FileInputStream
import java.util.Properties
import java.util.UUID

plugins {
    id("abacusflow-base")
    alias { libs.plugins.jooq.codegen }
}

dependencies {
    api(project(":abacusflow-infra:abacusflow-commons"))
    api(project(":abacusflow-infra:abacusflow-db"))
    api(libs.spring.boot.starter.jooq)
    jooqCodegen(libs.postgresql)
}

jooq {
    configuration {
        jdbc {
            driver = "org.postgresql.Driver" // JDBC 驱动类
            url = "jdbc:postgresql://${getProgramProperty("POSTGRES_HOST")}:${getProgramProperty("POSTGRES_PORT")}/${
                getProgramProperty("POSTGRES_DB")
            }"
            user = getProgramProperty("POSTGRES_USER")
            password = getProgramProperty("POSTGRES_PASSWORD")
        }

        generator {
            database {
                inputSchema = "public"
                excludes = "pg_catalog\\..*|information_schema\\..*"
            }
            target {
                packageName = "org.bruwave.abacusflow.generated.jooq"
                directory = "${projectDir}/build/generated/jooq/main"
            }
        }
    }
}

sourceSets["main"].kotlin.srcDir(projectDir.resolve( "build/generated/jooq/main").absolutePath)

tasks.compileKotlin {
    dependsOn(tasks.jooqCodegen)
}

fun getProgramProperty(name: String): String? {
    val properties = Properties()
    try {
        FileInputStream(File("${project.rootDir}/.env")).use { input ->
            properties.load(input)
        }
    } catch (e: Exception) {
        println("Error loading .env file: ${e.message}")
        return null
    }

    return properties.getProperty(name)
}
