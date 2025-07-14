// 在 build.gradle.kts 文件中添加以下代码

import java.io.File
import java.io.FileInputStream
import java.util.Properties
import org.gradle.api.Project
/**
 * 按优先级顺序获取配置值：
 * 1. 系统环境变量 (CI/shell)
 * 2. Gradle 属性 (gradle.properties / -P 参数)
 * 3. .env 文件
 */
fun Project.getEnvOrPropOrDotenv(key: String): String? =
    System.getenv(key)                     // ① CI / shell 环境变量
        ?: project.findProperty(key) as String?    // ② gradle.properties / -P 参数
        ?: getDotenvProperty(key)          // ③ .env 文件

/**
 * 从 .env 文件中读取指定属性
 */
fun Project.getDotenvProperty(key: String): String? {
    val envFile = File("${project.rootDir}/.env")
    if (!envFile.exists()) {
        return null
    }

    val properties = Properties()
    return try {
        FileInputStream(envFile).use { input ->
            properties.load(input)
        }
        properties.getProperty(key)
    } catch (e: Exception) {
        println("读取 .env 文件时出错: ${e.message}")
        null
    }
}

