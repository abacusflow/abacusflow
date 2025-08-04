import org.jooq.meta.jaxb.MatcherRule
import org.jooq.meta.jaxb.MatcherTransformType
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

plugins {
    id("abacusflow-base")
    alias { libs.plugins.jooq.codegen }
}

buildscript {
    dependencies {
        classpath(libs.testcontainers.postgresql) // 构建脚本类路径
    }
}
dependencies {
    api(project(":abacusflow-infra:abacusflow-commons"))
    api(project(":abacusflow-infra:abacusflow-db"))
    api(libs.spring.boot.starter.jooq)
    jooqCodegen(libs.postgresql)
}


// 直接覆盖 generateJooq 任务
tasks.named("jooqCodegen") {
    doFirst {
        val postgres = PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("abacusflow")
            .withUsername("abacusflow")
            .withPassword("abacusflow")

        project.extra["postgres"] = postgres

        postgres.start()

        // 执行初始化 SQL 脚本
        executeInitScripts(postgres)

        // 动态更新 JOOQ 配置
        project.extensions.configure<org.jooq.codegen.gradle.CodegenPluginExtension> {
            configurations {
                configuration {
                    jdbc {
                        driver = "org.postgresql.Driver"
                        url = postgres.jdbcUrl
                        user = postgres.username
                        password = postgres.password
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
                                        enumLiteral = MatcherRule()
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
        }

    }

    // 任务完成后清理容器
    doLast {
        val postgres = project.extra["postgres"] as PostgreSQLContainer<*>
        postgres.stop()
    }
}

sourceSets["main"].kotlin.srcDir(projectDir.resolve("build/generated/jooq/main").absolutePath)

tasks.compileKotlin {
    dependsOn(tasks.jooqCodegen)
}

// 执行初始化脚本的函数
fun executeInitScripts(postgres: PostgreSQLContainer<*>) {
    val scriptDir = file("${project.rootDir}/script/initdb")
    if (scriptDir.exists()) {
        scriptDir.listFiles { file -> file.extension == "sql" }
            ?.sortedBy { it.name }
            ?.forEach { sqlFile ->
                println("→ running ${sqlFile.name}")

                // 复制文件到容器
                postgres.copyFileToContainer(
                    org.testcontainers.utility.MountableFile.forHostPath(sqlFile.absolutePath),
                    "/tmp/${sqlFile.name}"
                )

                // 执行 SQL 脚本
                val result = postgres.execInContainer(
                    "psql",
                    "-U", postgres.username,
                    "-d", postgres.databaseName,
                    "-v", "ON_ERROR_STOP=1",
                    "-f", "/tmp/${sqlFile.name}"
                )

                if (result.exitCode != 0) {
                    throw GradleException("SQL script ${sqlFile.name} failed: ${result.stderr}")
                }
            }
    } else {
        println("⚠️  No init scripts found at ${scriptDir.absolutePath}")
    }
}