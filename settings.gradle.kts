plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "invenflow"

// 基础设施层
include("invenflow-infra:invenflow-db")
findProject(":invenflow-infra:invenflow-db")?.name = "invenflow-db"

// 领域核心层
include("invenflow-core:invenflow-user")
findProject(":invenflow-core:invenflow-user")?.name = "invenflow-user"

// 服务层
include("invenflow-usecase")
include("invenflow-usecase:invenflow-usecase-user")
findProject(":invenflow-usecase:invenflow-usecase-user")?.name = "invenflow-usecase-user"

// 接入层
include("invenflow-protal:invenflow-protal-web")
findProject(":invenflow-protal:invenflow-protal-web")?.name = "invenflow-protal-web"

// 辅助工具层
include("invenflow-tools:invenflow-monitor")
findProject(":invenflow-tools:invenflow-monitor")?.name = "invenflow-monitor"

// 应用服务启动层
include("invenflow-server")

//页面
include("invenflow-webapp")

