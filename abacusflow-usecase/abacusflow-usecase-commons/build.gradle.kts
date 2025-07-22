import org.gradle.kotlin.dsl.withType
import org.jooq.codegen.gradle.CodegenTask
import org.jooq.meta.jaxb.MatcherRule
import org.jooq.meta.jaxb.MatcherTransformType

val skipJooq = project.getEnvOrPropOrDotenv("skipJooq") == "true"

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
            driver = "org.postgresql.Driver"
            url = "jdbc:postgresql://${project.getEnvOrPropOrDotenv("POSTGRES_HOST") ?: "localhost"}:${project.getEnvOrPropOrDotenv("POSTGRES_PORT") ?: "5432"}/${project.getEnvOrPropOrDotenv("POSTGRES_DB") ?: "abacusflow"}"
            user = project.getEnvOrPropOrDotenv("POSTGRES_USER") ?: "abacusflow"
            password = project.getEnvOrPropOrDotenv("POSTGRES_PASSWORD")
        }

        generator {
            database {
                inputSchema = "public"
                excludes = "pg_catalog\\..*|information_schema\\..*"
            }
            target {
                packageName = "org.abacusflow.generated.jooq"
                directory = "${projectDir}/build/generated/jooq/main"
            }

            strategy {
                matchers {
                    enums {
                        enum_ {
                           // 默认匹配所有数据库 enum
                            enumLiteral =  MatcherRule()
                                .withTransform(MatcherTransformType.UPPER)
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

tasks.compileKotlin {
    dependsOn(tasks.jooqCodegen)
}

tasks.withType<CodegenTask>().configureEach {
    onlyIf{ !skipJooq }
}