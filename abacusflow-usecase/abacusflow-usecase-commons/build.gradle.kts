import org.jooq.codegen.gradle.CodegenTask
import org.jooq.meta.jaxb.MatcherRule
import org.jooq.meta.jaxb.MatcherTransformType
import java.io.FileInputStream
import java.util.Properties

val skipJooq = project.findProperty("skipJooq")?.toString() == "true"

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

            strategy {
                matchers {
                    enums {
                        enum_ {
//                          // 默认匹配所有数据库 enum
                            enumLiteral =  MatcherRule()
                                .withTransform(MatcherTransformType.UPPER)
//                                .withExpression("\$0")
                            enumClass = MatcherRule()
                                .withTransform(MatcherTransformType.PASCAL)
                                // db_enum -> DbEnum
                                .withExpression("\$0_db_enum")  // Append "DbEnum" suffix to enum class names
                        }
                    }
                }
            }
        }
    }
}

sourceSets["main"].kotlin.srcDir(projectDir.resolve("build/generated/jooq/main").absolutePath)

tasks.withType<CodegenTask>().configureEach {
    onlyIf{ !skipJooq }
}

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
