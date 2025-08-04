import org.gradle.declarative.dsl.schema.FqName.Empty.packageName
import org.jooq.codegen.gradle.CodegenPluginExtension
import org.jooq.meta.jaxb.MatcherRule
import org.jooq.meta.jaxb.MatcherTransformType
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.shaded.org.bouncycastle.cms.RecipientId.password
import org.testcontainers.utility.DockerImageName

val libsFun = versionCatalogs.named("libs")

plugins {
    kotlin("jvm")
    id("org.jooq.jooq-codegen-gradle")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    jooqCodegen(libsFun.findLibrary("postgresql").orElseThrow(::AssertionError))
}

// 创建扩展配置
open class JooqTestcontainerExtension {
    var dockerImage: String = "postgres:16-alpine"
    var databaseName: String = "abacusflow"
    var username: String = "abacusflow"
    var password: String = "abacusflow"
    var scriptPath: String = "script/initdb"
    var jooqConfigName: String = "main"

    // 存储用户的 generator 配置
    internal var generatorAction: Action<CodegenPluginExtension>? = null

    // DSL 方法，允许用户配置 generator
    fun generator(action: Action<CodegenPluginExtension>) {
        this.generatorAction = action
    }
}

// 创建扩展
val jooqTestcontainer = extensions.create<JooqTestcontainerExtension>("jooqTestcontainer")


// 配置任务
afterEvaluate {
    tasks.named("jooqCodegen") {
        doFirst {
            val postgres = createPostgresContainer(jooqTestcontainer)
            project.extra["postgres"] = postgres
            postgres.start()

            // 执行初始化脚本
            executeInitScripts(postgres, jooqTestcontainer)

            // 动态更新 JOOQ 配置
            updateJooqConfiguration(postgres, jooqTestcontainer)

            logger.info("🐘 PostgreSQL Testcontainer started: ${postgres.jdbcUrl}")
        }

        doLast {
            if (project.extra.has("postgres")) {
                val postgres = project.extra["postgres"] as PostgreSQLContainer<*>
                postgres.stop()
                logger.info("🛑 PostgreSQL Testcontainer stopped")
            }
        }
    }
}


// 工具函数
fun createPostgresContainer(extension: JooqTestcontainerExtension): PostgreSQLContainer<*> {
    return PostgreSQLContainer(DockerImageName.parse(extension.dockerImage))
        .withDatabaseName(extension.databaseName)
        .withUsername(extension.username)
        .withPassword(extension.password)
}

fun executeInitScripts(
    postgres: PostgreSQLContainer<*>,
    extension: JooqTestcontainerExtension
) {
    val scriptDir = File(rootProject.rootDir, extension.scriptPath)
    if (!scriptDir.exists()) {
        logger.warn("⚠️  Init script directory not found: ${scriptDir.absolutePath}")
        return
    }

    scriptDir.listFiles { file -> file.extension == "sql" }
        ?.sortedBy { it.name }
        ?.forEach { sqlFile ->
            logger.info("→ running ${sqlFile.name}")

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
}

fun updateJooqConfiguration(
    postgres: PostgreSQLContainer<*>,
    extension: JooqTestcontainerExtension
) {
    extensions.configure<org.jooq.codegen.gradle.CodegenPluginExtension> {
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
        // 🔥 关键：应用用户自定义配置
        extension.generatorAction?.execute(this)
    }
}

sourceSets["main"].java.srcDir(projectDir.resolve("build/generated/jooq/main"))

tasks.compileKotlin {
    dependsOn(tasks.jooqCodegen)
}
