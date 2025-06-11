plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "abacusflow"

// 基础设施层
include("abacusflow-infra:abacusflow-db")
findProject(":abacusflow-infra:abacusflow-db")?.name = "abacusflow-db"
include("abacusflow-infra:abacusflow-commons")
findProject(":abacusflow-infra:abacusflow-commons")?.name = "abacusflow-commons"

// 领域核心层
include("abacusflow-core:abacusflow-user")
findProject(":abacusflow-core:abacusflow-user")?.name = "abacusflow-user"

// 服务层
// include("abacusflow-usecase")
include("abacusflow-usecase:abacusflow-usecase-commons")
findProject(":abacusflow-usecase:abacusflow-usecase-user")?.name = "abacusflow-usecase-commons"
include("abacusflow-usecase:abacusflow-usecase-user")
findProject(":abacusflow-usecase:abacusflow-usecase-user")?.name = "abacusflow-usecase-user"

// 接入层
include("abacusflow-protal:abacusflow-protal-web")
findProject(":abacusflow-protal:abacusflow-protal-web")?.name = "abacusflow-protal-web"

// 辅助工具层
include("abacusflow-tools:abacusflow-monitor")
findProject(":abacusflow-tools:abacusflow-monitor")?.name = "abacusflow-monitor"

// 应用服务启动层
include("abacusflow-server")

// 页面
include("abacusflow-webapp")
